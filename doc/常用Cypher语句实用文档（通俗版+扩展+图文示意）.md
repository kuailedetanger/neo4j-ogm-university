# 常用Cypher语句实用文档（通俗版+扩展+图文示意）

Cypher是Neo4j图数据库的查询语言，核心围绕“节点”和“关系”操作，语法贴近自然语言，以下按高频场景分类讲解，兼顾基础用法、扩展场景，重点包含按ID删除关系的语句，用【图文示意】模拟图结构，方便快速理解。

## 一、基础概念铺垫

- **节点**：图数据库的核心元素，代表一个实体（如用户、商品、订单），可带标签（分类）和属性（特征）。

- **关系**：连接两个节点的纽带，必须有方向和类型（如“关注”“购买”“属于”），也可带属性（如关系创建时间）。

- **ID**：节点和关系的内置唯一标识（由Neo4j自动生成，不可修改），节点ID用`id(node)`获取，关系ID用`id(relationship)`获取。

提示：实际使用中，建议给节点/关系添加业务唯一标识（如用户ID、订单号），内置ID仅用于临时定位或紧急操作。

## 二、节点操作语句（创建、查询、更新、删除）

### 1. 创建节点

**基础语法**：创建带标签和属性的节点

```cypher

// 创建单个用户节点（标签：User，属性：姓名、年龄、性别）
CREATE (u:User {name: "张三", age: 28, gender: "男"})
RETURN u; // RETURN用于返回创建的节点，便于验证
```

**扩展场景**：一次性创建多个节点+指定业务ID

```cypher

CREATE 
  (u1:User {userId: "U001", name: "张三", age: 28}),
  (u2:User {userId: "U002", name: "李四", age: 32}),
  (p1:Product {prodId: "P001", name: "无线耳机", price: 599})
RETURN u1, u2, p1;
```

**图文示意**：生成3个独立节点，无关联关系

[User(U001,张三)]  [User(U002,李四)]  [Product(P001,无线耳机)]

### 2. 查询节点

**基础语法**：按标签、属性查询

```cypher

// 查询所有User节点
MATCH (u:User)
RETURN u.name, u.age; // 只返回姓名和年龄，减少数据传输

// 按属性条件查询（查询年龄>30的用户）
MATCH (u:User)
WHERE u.age > 30
RETURN u;
```

**扩展场景**：按内置ID查询节点

```cypher

// 查询ID为123的节点（无论标签）
MATCH (n)
WHERE id(n) = 123
RETURN n;
```

### 3. 更新节点属性

```cypher

// 更新张三的年龄为29，新增邮箱属性
MATCH (u:User {name: "张三"})
SET u.age = 29, u.email = "zhangsan@xxx.com"
RETURN u;
```

### 4. 删除节点

**注意**：若节点存在关系，需先删除关系才能删除节点（否则报错）

```cypher

// 先删除张三的所有关系，再删除节点
MATCH (u:User {name: "张三"})-[r]-() // 匹配张三的所有关系（无方向）
DELETE r, u;
```

## 三、关系操作语句（创建、查询、更新、删除，含按ID删除）

### 1. 创建关系

**基础语法**：连接两个已存在的节点，创建带类型和属性的关系

```cypher

// 张三购买了无线耳机，创建“购买”关系，添加购买时间和数量属性
MATCH (u:User {name: "张三"}), (p:Product {name: "无线耳机"})
CREATE (u)-[b:BUY {buyTime: "2026-01-23", quantity: 1}]->(p) // ->表示关系方向（用户到商品）
RETURN u, b, p;
```

**图文示意**：节点间建立关联关系

[User(张三)] -[BUY(buyTime:2026-01-23,quantity:1)]-> [Product(无线耳机)]

**扩展场景**：创建关系时若节点不存在，自动创建节点（慎用，避免生成无效节点）

```cypher

// 李四收藏了手机（手机节点不存在则创建）
MERGE (u:User {name: "李四"})-[c:COLLECT {collectTime: "2026-01-23"}]->(p:Product {name: "手机"})
RETURN u, c, p;
```

### 2. 查询关系

```cypher

// 查询张三的所有购买关系及关联商品
MATCH (u:User {name: "张三"})-[b:BUY]->(p:Product)
RETURN u.name, b.buyTime, p.name;

// 查询所有BUY类型的关系
MATCH ()-[b:BUY]-()
RETURN id(b) AS relId, b.buyTime; // 返回关系ID和购买时间
```

### 3. 更新关系属性

```cypher

// 更新张三购买无线耳机的数量为2
MATCH (u:User {name: "张三"})-[b:BUY]->(p:Product {name: "无线耳机"})
SET b.quantity = 2
RETURN b;
```

### 4. 删除关系（核心：含按ID删除）

删除关系无需先删除节点，直接匹配关系即可删除，按删除条件分为以下场景：

#### （1）按关系类型/属性删除

```cypher

// 删除张三购买无线耳机的BUY关系
MATCH (u:User {name: "张三"})-[b:BUY]->(p:Product {name: "无线耳机"})
DELETE b;
```

#### （2）按内置ID删除关系（重点需求）

先通过查询获取关系ID，再精准删除（避免误删其他关系）

```cypher

// 步骤1：查询目标关系的ID（假设已知关联节点，缩小范围）
MATCH (u:User {name: "张三"})-[b:BUY]->(p:Product)
RETURN id(b) AS relId, p.name; // 假设查询到relId=456

// 步骤2：按ID删除关系
MATCH ()-[r]-()
WHERE id(r) = 456 // 传入查询到的关系ID
DELETE r;
```

警告：按ID删除关系时，务必先验证ID对应的关系是否正确，内置ID不具备业务含义，避免删错。

#### （3）删除节点的所有关系

```cypher

// 删除李四的所有入站和出站关系
MATCH (u:User {name: "李四"})-[r]-()
DELETE r;
```

## 四、复杂查询语句（扩展常用场景）

### 1. 多关系联查（查询用户购买的商品所属分类）

```cypher

// 张三购买的商品属于哪个分类
MATCH (u:User {name: "张三"})-[b:BUY]->(p:Product)-[b2:BELONG_TO]->(c:Category)
RETURN u.name, p.name, c.name;
```

**图文示意**：多节点多关系联查链路

[User(张三)] -[BUY]-> [Product(无线耳机)] -[BELONG_TO]-> [Category(数码产品)]

### 2. 统计查询（统计每个分类的商品被购买次数）

```cypher

MATCH (p:Product)-[b:BUY]-(u:User), (p)-[:BELONG_TO]->(c:Category)
RETURN c.name AS categoryName, COUNT(b) AS buyCount
ORDER BY buyCount DESC;
```

## 五、实用技巧与注意事项

- **MERGE与CREATE的区别**：CREATE强制创建新节点/关系（可能重复），MERGE先匹配，不存在再创建（避免重复数据），适合初始化和去重场景。

- **关系方向**：查询时可省略方向（用-[r]-代替->），但创建时建议明确方向，保证业务逻辑清晰。

- **性能优化**：对高频查询的属性（如userId、prodId）创建索引，减少匹配时间，语法：`CREATE INDEX idx_user_userId FOR (u:User) ON (u.userId);`。

- **事务性**：复杂操作（如先删关系再删节点）会自动开启事务，要么全部成功，要么全部失败，无需手动控制。



## 个人学习资源总结：

ogm 官网： https://neo4j.com/docs/ogm-manual/3.2/reference/#reference:ha

**Neo4j 官方教程中文版** ： http://neo4j.com.cn/topic/67f4cf91455186f56d9f412f



