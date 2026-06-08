# Neo4j零基础教程｜第四节：从MySQL（关系库）过渡到图数据库

> 参考原文地址：[http://neo4j\.com\.cn/topic/67f4cf71455186f56d9f412e](http://neo4j.com.cn/topic/67f4cf71455186f56d9f412e)
> 适配小白学习，结合你**企业股权图谱业务**，对比 SQL 与 Cypher、建模差异、项目落地方式，承接前三节属性图知识点
> 
> 

## 目录

1. 关系型数据库（RDBMS）核心存储特点

2. 图数据库与关系库：底层存储本质区别

3. 同一业务：SQL 建模 VS Neo4j 图建模（人员 \- 部门案例）

4. 同需求：SQL 语句 VS Cypher 语句直观对照

5. 从 MySQL 迁移 Neo4j 落地步骤（代码示例）

6. 结合你的企业业务选型总结

7. 随堂练习

## 一、关系型数据库（MySQL）核心特点

1. **固定表结构**：建表提前定义字段、字段类型，后续新增字段需要`ALTER`改表；

2. **靠主键 \+ 外键关联实体**：两张表关联必须存储主键 ID，多对多场景强制新建**中间关联表**；

3. **关联查询依赖 JOIN**：多表连接靠笛卡尔积 \+ 条件过滤，**关联层级越深，性能下滑越严重（指数级损耗）**；

4. 适用场景：单据、台账、单表统计（订单、商品库存等简单业务），**不适合多层股权、人脉链路查询**。

> 举例：人员和部门多对多，MySQL 必须三张表：`person(人员)`、`department(部门)`、`person_dept(中间关联表)`。
> 
> 

## 二、图数据库与关系库本质差异

|对比维度|MySQL \(关系型\)|Neo4j \(图数据库\)|
|---|---|---|
|实体存储|数据表、行记录|节点（Node）|
|实体关联|外键 \+ 中间关联表|**原生关系（Relationship），数据库物理存储关联链路**|
|多对多处理|强制新建中间表|直接创建关系，无需额外表|
|关联查询开销|多层 JOIN，性能随层级暴跌|顺着关系遍历，层数几乎不影响性能|
|字段灵活性|建表固定字段，改结构需 DDL|无 Schema 约束，节点随时新增属性|
|查询语言|SQL|Cypher（借鉴 SQL 语法，图专属）|

**核心关键点**：MySQL「数据和关系分开存，查询时临时拼接」；Neo4j「实体 \+ 关联一起落地存储，查询直接顺着边走」。

## 三、同一业务：SQL 建模 VS 图建模（人员 \- 部门案例）

### 3\.1 MySQL 建模方案（3 张表）

1. `person(id,name)`：人员主键 id、姓名

2. `department(id,name)`：部门主键 id、部门名称

3. `person_dept(pid,did)`：中间表，存储人员 id、部门 id，实现多对多

> 查询 Alice 所属 3 个部门：先查 Alice 主键→中间表匹配所有 did→根据 did 查部门表，三次查表 \+ 两次 JOIN。
> 
> 

### 3\.2 Neo4j 图建模方案（仅两类节点 \+ 关系）

- 节点：`Person{name:"Alice"}`、`Department{name:"4Future"}`

- 关系：`(p:Person)-[:BELONGS_TO]->(d:Department)`（归属）

> 查询 Alice 所属部门：找到 Alice 节点，直接遍历所有出方向`BELONGS_TO`关系，一步到位，无中间数据。
> 
> 

## 四、同需求：SQL 与 Cypher 语句对照（查 IT 部门所有员工）

### 4\.1 MySQL\-SQL 写法（3 表 JOIN）

```sql
SELECT p.name FROM Person p
LEFT JOIN Person_Department pd ON p.Id = pd.PersonId
LEFT JOIN Department d ON pd.DepartmentId = d.Id
WHERE d.name = "IT Department";
```

> 缺点：多 JOIN，数据量大、部门多的时候查询变慢；新增人员和部门关联必须插入中间表。
> 
> 

### 4\.2 Neo4j\-Cypher 写法（直观匹配图结构）

```cypher
MATCH (p:Person)-[:WORKS_AT]->(d:Dept)
WHERE d.name = "IT Department"
RETURN p.name
```

> 优势：语法和现实逻辑一致，新增关联只需要新增一条关系，无需改表、无需维护中间表。
> 
> 

### 拓展：适配你的企业业务对照

需求：查询 A 集团直接 / 通过自然人参股的企业（你 1 度业务）

- MySQL：企业表、自然人表、投资中间表，多层 JOIN，多场景拼接 SQL；

- Cypher：`MATCH(start:Enterprise{creditCode:$code})--()--(end:Enterprise) WHERE start<>end RETURN DISTINCT end`，单语句搞定。

## 五、项目从 MySQL 迁移到 Neo4j 实操

### 5\.1 应用接入方式（和 JDBC 用法高度相似，小白易上手）

Neo4j 支持 JDBC 驱动，原有 JDBC 编程逻辑几乎不用大改，仅替换 SQL 为 Cypher：

```java
// JDBC连接Neo4j示例
Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
String cypher = "MATCH (:Person {name:{1}})-[:EMPLOYEE]-(d:Department) RETURN d.name as dept";
try (PreparedStatement stmt = con.prepareStatement(cypher)) {
    stmt.setString(1,"John");
    ResultSet rs = stmt.executeQuery();
    while(rs.next()) {
        String deptName = rs.getString("dept");
    }
}
```

### 5\.2 SpringBoot 项目接入（你在用方案）

1. 少量改造：MySQL→Neo4j，DAO 层从 Mybatis 换成`Neo4jTemplate/Neo4jRepository`；

2. 数据迁移：MySQL 全量数据导出 CSV，用`LOAD CSV`批量导入 Neo4j 生成节点 \& 关系。

## 六、结合你的企业图谱业务选型总结

1. **企业基础信息（名称、信用代码、注册资金）**：少量单表查询、统计，可继续存 MySQL；

2. **企业投资、参股、自然人持股、股权穿透、1 度关联查询**：全部落地 Neo4j，规避多层 JOIN，使用前文 1 度 Cypher 语句；

3. 建模规则：

    - 企业→`Enterprise`节点，自然人→`Person`节点；

    - 投资、控股→`:INVEST`、`:HOLD_SHARE`关系，关系携带投资金额等属性。

## 七、随堂实操练习

### 需求：员工 \- 部门数据创建 \+ 查询

1. Cypher 创建数据

```cypher
CREATE (p:Person{name:"张三"})-[:WORKS_AT]->(d:Dept{name:"IT Department"});
CREATE (p2:Person{name:"李四"})-[:WORKS_AT]->(d);
```

2. 查询 IT 部门全部人员（复用上面 Cypher 模板）

## 八、生产级避坑：防止Cypher查询数据量过大、炸库、拖垮数据库

**适配场景**：企业图谱、1度关联查询、节点属性多、高频查询、极易出现超大结果集拖垮服务

**核心痛点**：业务图谱链路复杂，单次查询可能命中成千上万个节点/关系，返回数据量爆炸、网络传输超时、CPU打满，严重时直接拖垮Neo4j数据库。

### 8\.1 先验证：你当前的思路是否正确？

#### 你现在的方案

查询前先 **COUNT\(关系\)** 预判数据量，不敢 COUNT\(节点\)，因为觉得节点计数慢。

#### 结论：思路**半对、存在漏洞、不完全安全**

##### ✅ 正确的部分

- 查询前做「数据量预判」是生产必须的规范，能避免盲目执行超大查询；

- COUNT\(关系\) 确实比 COUNT\(节点\) **速度更快、开销更低**，你的感知完全没问题。

##### ❌ 存在的核心漏洞（关键隐患）

1. **统计关系数量 ≠ 控制返回节点数量**
一条节点链路会包含多条关系，存在「关系极多、最终节点很少」或「关系少、节点批量重复」的情况，统计关系无法精准预判最终返回的数据体量，防不住炸库场景。

2. **无法去重预判**
业务1度查询存在大量重复节点，COUNT 预统计是原始数据量，不是 DISTINCT 去重后的真实数据量，预判结果失真。

3. **极端场景失效**
少量关系、大批量节点的特殊图谱场景，会出现预判通过、实际查询炸库的问题。

#### 最终总结你的思路

**COUNT\(关系\) 可以做辅助预判，但不能作为唯一限流手段；COUNT\(节点\) 慢是正常现象，不适合前置预判，完全不用舍弃、也不用强行使用。**

---

### 8\.2 最优、最高性能、生产通用的防炸库方案（无需前置COUNT）

Neo4j 官方推荐、企业级项目通用：**不做前置count，直接在Cypher内部做「硬限流」**

优势：**零额外查询开销、绝对安全、语法极简、适配所有图谱查询**

#### 核心语法：LIMIT 强制截断结果集

不管数据库匹配到1万条、10万条数据，**数据库层直接截断，不会返回超大结果、不会占用网络、不会炸库**。

##### 适配你的【业务1度查询】终极安全版（可直接上线）

```cypher
// 带字段精简、去重、强制限流、防炸库 完整版
MATCH (start:Enterprise {creditCode:$code})--()--(end:Enterprise)
WHERE start <> end
RETURN DISTINCT 
    end.creditCode AS creditCode,
    end.name AS name,
    end.status AS status
LIMIT 200

```

释义：无论匹配多少数据，**最多只返回200条**，彻底杜绝网络超限、数据库CPU打满、接口超时问题。

---

### 8\.3 三种方案性能对比（帮你彻底选对）

|方案|性能|安全性|适用场景|
|---|---|---|---|
|前置 COUNT\(关系\) 预判（你的方案）|较快|一般（有漏洞）|辅助参考，不能单独使用|
|前置 COUNT\(节点\) 预判|慢、开销大|高|不推荐，浪费性能|
|**Cypher 内置 LIMIT 硬限流（最优）**|**零额外开销、最快**|**绝对安全**|**生产唯一推荐**|

---

### 8\.4 进阶：精准解决你的所有顾虑（全覆盖方案）

#### 场景1：既想要安全限流，又想知道是否数据被截断（友好提示）

业务需求：限制200条，同时前端可判断「是否还有更多数据」，避免数据丢失感知。

```cypher
MATCH (start:Enterprise {creditCode:$code})--()--(end:Enterprise)
WHERE start <> end
WITH DISTINCT end AS list
RETURN 
    collect(list.creditCode)[0..199] AS dataList,
    count(list) > 200 AS hasMore

```

效果：返回当前页数据 \+ 是否有更多标记，完美适配列表查询。

#### 场景2：超高并发、密集查询极致优化

配合你的索引 \+ 精简指定字段 \+ 限流，三重保障：

1. 索引加速匹配（已有）

2. 只返回需要字段，减少网络体积

3. LIMIT 强制截断，杜绝超大结果

---

### 8\.5 最终落地结论（直接照做）

1. **放弃前置COUNT预判**：无论是count节点还是count关系，都属于多余开销，且存在安全漏洞；

2. **统一使用 Cypher LIMIT 硬限流**：Neo4j官方最优防炸库方案，零开销、全覆盖；

3. 结合「DISTINCT去重 \+ 按需返回指定字段 \+ 索引」，构成你项目的**生产级安全查询规范**；

4. 你的业务1度查询、企业关联查询，全部替换为带 LIMIT 限流版本。

## 九、下一节预习

Cypher 常用关键字：WHERE、DISTINCT、LIMIT、ORDER BY 基础用法。

Cypher 常用关键字：WHERE、DISTINCT、LIMIT、ORDER BY 基础用法。

> （注：文档部分内容可能由 AI 生成）
