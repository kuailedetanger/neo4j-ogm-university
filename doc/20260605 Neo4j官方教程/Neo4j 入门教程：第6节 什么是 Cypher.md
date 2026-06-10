# Neo4j 入门教程：第6节 什么是 Cypher

原文参考地址：[http://blog\.neo4j\.com\.cn/topic/67f5cb22455186f56d9f4131](http://blog.neo4j.com.cn/topic/67f5cb22455186f56d9f4131)

## 一、Cypher 简介

### 1\. 什么是 Cypher

Cypher 是 **Neo4j 图数据库**专属的查询语言，具备以下特点：

1. 声明式语法、兼容 GQL 标准，基于 `openCypher` 开源项目；

2. 对标关系型数据库的 SQL，但**专门为图数据优化**；

3. 语法贴近自然语言，可视化强、上手简单；

4. 支持对图数据实现**增、查、改、删（CRUD）** 全操作。

基础可视化语法格式：

```cypher
(:节点)-[:关系]->(:其他节点)
```

> 新手无需本地安装 Neo4j，可使用官方免费 Aura 在线实例，直接练习 Cypher。
> 
> 官方免费在线练习地址（永久有效、无需本地安装，小白直接用）：[https://console\.neo4j\.io/](https://console.neo4j.io/)
> 说明：该地址为 Neo4j Aura 官方免费云实例控制台，支持免费创建数据库、在线运行 Cypher 语句、交互式练习，无需信用卡，零部署成本，完全适配新手入门练习。
> 
> 

### 2\. Cypher 工作原理

Neo4j 图数据核心由**节点**和**关系**组成，二者都可以挂载自定义属性。
Cypher 核心能力是**模式匹配**，通过识别节点、关系组成的图模式，完成数据查询与操作，这也是它易学习的核心原因。

## 二、Cypher 核心语法基础

图示例说明：Sally 喜欢 Graphs、和 John 是朋友、为 Neo4j 工作。
对应自然语言：`Sally 喜欢图。Sally 和 John 是朋友。Sally 为 Neo4j 工作。`

### 1\. 节点（Node）

节点用来表示实体、名词、对象，是图数据的基础单元。

#### 1\.1 节点基础写法

Cypher 中使用**英文小括号 ****`()`** 包裹节点：

```cypher
// 基础节点写法
()
// 带标签的节点（推荐）
(:Sally)
(:John)
```

#### 1\.2 节点标签（Label）

标签用于**分组归类节点**，作用类似 SQL 中的数据表，可优化查询效率。

- 格式：`(变量:标签名)`

- 作用：筛选指定类型实体，不加标签会遍历全库所有节点，大数据场景性能差。

示例分类：

- 人物节点：`Person`（Sally、John）

- 技术节点：`Technology`（Graphs）

- 公司节点：`Company`（Neo4j）

#### 1\.3 节点变量

可以给节点定义**变量**，后续语句可重复引用该节点，变量建议小写。

- 语法：`(变量名:标签名)`

示例对比：

|无变量写法|有变量写法|
|---|---|
|\\`\`\`cypher||
|MATCH \(:Person\)||
|RETURN Person||
|\\`\`\`|\\`\`\`cypher|
|MATCH \(p:Person\)||
|RETURN p||

```|
> 注意：`(Person)` 不带冒号时，`Person` 会被识别为**变量**，而非标签。标签前必须加冒号 `:`。

### 2. 关系（Relationship）
关系用于描述**节点与节点之间的关联**，是图数据库的核心优势。

#### 2.1 关系基础写法
使用**方括号 `[]`** 定义关系，搭配箭头表示连接，基础格式：
```cypher
(节点1)-[]->(节点2)
```

#### 2\.2 关系类型

关系类型用来定义关联含义，**建议使用动词**描述关系，类型前必须加冒号 `:`。
示例：

```cypher
// Sally 喜欢 Graphs
(:Sally)-[:LIKES]->(:Graphs)
// Sally 和 John 是朋友
(:Sally)-[:IS_FRIENDS_WITH]->(:John)
// Sally 为 Neo4j 工作
(:Sally)-[:WORKS_FOR]->(:Neo4j)
```

> 易错点：`[LIKES]` 不带冒号时，`LIKES` 是**关系变量**，不是关系类型。
> 
> 

#### 2\.3 关系方向

关系默认带方向，Cypher 支持三种方向写法：

1. **正向（左→右）**

    ```cypher
    (p:Person)-[:LIKES]->(t:Technology)
    ```

2. **反向（右→左）**

    ```cypher
    (p:Person)<-[:LIKES]-(t:Technology)
    ```

3. **无向（不限制方向）**
仅用于查询，创建关系依然有方向；不知道关系方向时优先使用。

    ```cypher
    MATCH (p:Person)-[:LIKES]-(t:Technology)
    ```

> 提示：无向查询会双向遍历，相同数据会重复返回，大数据场景需留意性能。
> 
> 

#### 2\.4 关系变量

和节点一样，关系也可以定义变量，方便后续引用：

```cypher
// p=人物节点变量，r=关系变量，t=技术节点变量
MATCH (p:Person)-[r:LIKES]->(t:Technology)
RETURN p, r, t
```

### 3\. 属性（Property）

节点和关系都可以添加**自定义属性**，用来存储实体详情，使用**大括号 ****`{}`** 包裹属性。

- 格式：`{属性名: '属性值'}`，值支持单 / 双引号包裹。

#### 3\.1 节点添加属性

示例：创建两个带 `name` 属性的人物节点，并建立朋友关系

```cypher
CREATE (p1:Person {name:'Sally'})-[r:IS_FRIENDS_WITH]->(p2:Person {name:'John'})
RETURN p1, r, p2
```

#### 3\.2 带属性的完整模式

结合**节点 \+ 标签 \+ 属性 \+ 关系**组成完整图模式：

```cypher
(p:Person {name: "Sally"})-[r:LIKES]->(g:Technology {type: "Graphs"})
```

## 三、Cypher 常用基础语句

图模式需要搭配查询 / 操作语句才能执行，下面介绍两个最基础核心语句。

### 1\. CREATE 语句（创建节点 / 关系）

作用：向图数据库**新增**节点、关系、属性。

```cypher
// 创建 Sally 喜欢 Graphs 的完整图结构
CREATE (p:Person {name: "Sally"})-[r:LIKES]->(t:Technology {type: "Graphs"})
```

### 2\. MATCH \+ RETURN 语句（查询数据）

作用：**匹配图模式**并返回查询结果，是最常用的查询组合。

```cypher
// 查询 Sally 喜欢的技术节点
MATCH (p:Person {name: "Sally"})-[r:LIKES]->(t:Technology {type: "Graphs"})
RETURN p, r, t
```

## 四、补充学习指引

1. **核心学习方向**

    - 掌握各类查询子句、函数、数据类型；

    - 深入理解图模式匹配、图数据导航；

    - 图数据建模思路。

2. **不同技术栈过渡**

    - 有 SQL 基础：可对比 Cypher 与 SQL 差异，学习关系型数据向图数据迁移；

    - 有 NoSQL 基础：学习文档型数据库转图数据库的建模方式。

3. **在线练习**
推荐 Neo4j GraphAcademy 免费课程，60 分钟入门 Cypher，提供在线沙箱环境，无需本地部署。

## 五、知识点总结（小白速记）

1. 节点：`()`，可加标签 `(变量:标签)`、属性 `{key:value}`；

2. 关系：`-[:关系类型]->`，支持正向、反向、无向三种方向；

3. 标签 / 关系类型**前必须加冒号**，否则会被识别为变量；

4. 变量可复用节点 / 关系，简化查询语句；

5. 核心语句：`CREATE`（创建）、`MATCH`（匹配查询）、`RETURN`（返回结果）。

```Plain Text

```

> （注：文档部分内容可能由 AI 生成）
