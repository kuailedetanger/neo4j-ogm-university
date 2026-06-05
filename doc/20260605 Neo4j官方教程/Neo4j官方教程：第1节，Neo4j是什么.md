# Neo4j官方教程：第1节，Neo4j是什么

# Neo4j 零基础小白入门教程（Markdown 版）

> 参考：Neo4j 中文社区官方入门文档，适配**Neo4j 3\.4\.19（本地在用版本）**，兼顾基础理论 \+ 环境安装 \+ Cypher 入门 \+ SpringBoot 整合方向，适合零基础学生循序渐进学习
> 
> 

## 目录

1. 什么是 Neo4j（图数据库基础概念）

2. Neo4j 产品生态与部署方式

3. 本地 Neo4j3\.4\.19 安装 \& 启动教程（重点）

4. 核心四大基础概念：节点 / 标签 / 属性 / 关系

5. Cypher 入门：增删改查实操（小白可直接复制运行）

6. Neo4j 配套工具与开发 SDK（OG、SpringDataNeo4j）

7. SpringBoot 整合 Neo4j3\.4\.19 入门指引

8. 课后实操练习题

## 一、什么是 Neo4j

### 1\. 定义

Neo4j 是**原生属性图数据库**，底层存储采用图结构，不是在 MySQL 等关系库之上伪装图模型，数据存储逻辑和我们手绘的实体关系图完全一致。

- 开发语言：Java\+Scala 开源实现，社区版免费商用；

- 核心能力：完整支持**ACID 事务、集群部署、故障自动转移**，区别于普通 NoSQL 数据库；

- 适用场景：社交关系、知识图谱、供应链、金融股权链路、推荐算法（多深度关联查询），**SQL 多层连表很难实现的场景首选**。

### 2\. 对比关系型数据库（小白快速理解）

|特点|MySQL \(关系库\)|Neo4j \(图数据库\)|
|---|---|---|
|存储结构|表、行、字段，靠外键关联|节点 \+ 带方向的关系 \+ 属性，天然存关联|
|多深度查询|多层 JOIN，数据量大性能暴跌|顺着关系遍历，层级越多优势越大|
|建表约束|建表必须提前定义字段|无固定 Schema，随时新增属性 / 关系|

### 3\. 查询语言：Cypher

Neo4j 专属类 SQL 声明式查询语言，语法贴近自然语言，`openCypher`开源规范被 HANA 等多款数据库兼容。

## 二、Neo4j 产品生态与 4 种部署方案

### 1\. 四种部署选型（按需选择）

1. **AuraDB 云端托管**：Neo4j 官方云服务，开箱即用无需安装，免费额度适合练手，包含图算法库 AuraDS；

2. **自托管云部署**：AWS/Azure/GCP 云服务器手动部署；

3. **本地单机部署（学生首选，你当前 3\.4\.19）**：Windows/Linux/mac 本地安装社区版，日常开发调试；

4. **容器部署**：Docker/K8s 部署，生产集群常用（Helm 一键部署单机 / 集群）。

### 2\. 生态配套组件

- **GDS 图算法库**：内置社区发现、最短路径等 65 \+ 图算法，用于关系挖掘、缺失关系预测；

- **OGM 对象图映射**：类似 MyBatis/JPA，Java 实体自动映射节点 \& 关系（对接 SpringBoot 核心）；

- **三大 API**：HTTP 接口、CDC 数据变更捕获 API、原生驱动 API（Java/Python/Go）。

## 三、本地 Neo4j3\.4\.19 安装 \& 启动（适配你的环境，JDK8）

> ⚠️ 版本硬性约束：Neo4j3\.4\.x **必须 JDK1\.8**，不能 JDK11/17；端口：7474 \(网页\)、7687 \(Bolt 连接\)
> 
> 

### 3\.1 Windows 安装步骤

1. 下载`neo4j-community-3.4.19.zip`压缩包，解压到**无中文、无空格路径**（如`D:\dev\neo4j3.4.19`）；

2. CMD 进入 bin 目录：`cd D:\dev\neo4j3.4.19\bin`

    ```bash
    # 前台启动（关闭CMD即停止，调试用）
    neo4j console
    # 安装系统服务+后台启动（推荐）
    neo4j install-service
    neo4j start
    neo4j stop #停止服务
    ```

3. 浏览器访问：`http://127.0.0.1:7474`

    - 默认账号：`neo4j`，默认密码：`neo4j`，首次登录强制修改密码；

    - Java 项目连接地址：`bolt://127.0.0.1:7687`。

### 3\.2 Linux（Centos/Ubuntu）启动

```bash
# 解压后进入bin
./neo4j start
./neo4j stop
```

## 四、四大核心概念（重中之重，对标 SQL）

|名词|英文|通俗解释|SQL 对标|Cypher 书写格式|
|---|---|---|---|---|
|节点|Node|现实实体（人 / 商品 / 公司）|表中的一条数据|`(p:Person{name:"张三",age:20})`|
|标签|Label|节点分类，一个节点可多标签|数据表|`:Person :Student`|
|属性|Property|节点 / 关系的字段信息（键值对）|字段|`{name:"李四"}`|
|关系|Relationship|节点之间的关联（有方向、可带属性）|外键关联|`(a)-[r:FRIEND{level:1}]->(b)`|

### 示例：张三和李四是好友

- 2 个节点：张三 \(Person\)、李四 \(Person\)

- 1 条有向关系：`张三-[好友]->李四`，关系属性：亲密度 = 5

```cypher
CREATE (p1:Person{name:"张三"})-[:FRIEND{intimacy:5}]->(p2:Person{name:"李四"})
```

## 五、Cypher 基础 CRUD 实操（复制到 7474 控制台直接运行）

> 关键字：`CREATE新增、MATCH查询、SET修改、DELETE删除`
> 
> 

### 5\.1 新增节点 \& 关系

```cypher
// 新增单个用户节点
CREATE (u:User{name:"小明",age:22,phone:"13800001111"})

// 批量创建+创建关系：小明关注小红
CREATE (u1:User{name:"小明"})-[:FOLLOW]->(u2:User{name:"小红"})
```

### 5\.2 数据查询（MATCH 核心）

```cypher
// 查询所有User标签节点
MATCH (u:User) RETURN u

// 按姓名精准查询
MATCH (u:User{name:"小明"}) RETURN u.name,u.age

// 查询小明关注的所有人（顺着FOLLOW关系遍历）
MATCH (u1:User{name:"小明"})-[:FOLLOW]->(u2) RETURN u2.name
```

### 5\.3 修改数据（SET 更新属性）

```cypher
MATCH (u:User{name:"小明"}) SET u.age=23 RETURN u
```

### 5\.4 删除（分删属性、删关系、删节点）

```cypher
// 删除属性
MATCH (u:User{name:"小明"}) REMOVE u.phone
// 删除关系
MATCH ()-[r:FOLLOW]->() DELETE r
// 删除节点（节点有关联关系必须先删关系）
MATCH (u:User{name:"小红"}) DELETE u
```

### 5\.5 快速清空全库（测试用）

```cypher
MATCH (n) DETACH DELETE n
```

## 六、开发 SDK 与工具（对接 Java 开发）【重点进阶：场景选型\+性能优化】

针对核心业务场景：**企业节点属性多、查询密集、高频查询、既要开发效率高、又要避免原生手写Cypher低效冗余**，本节深度拆解三种Java操作Neo4j方案，精准区分适用场景、性能差异、优缺点，解决选型困惑，适配生产高频查询业务。

目前适配 **Neo4j 3\.4\.19 \+ SpringBoot2\.1\.x** 的三种核心开发方式：

1. SpringDataNeo4j Repository（SDN 封装层）

2. Neo4jTemplate（中间折中封装层）

3. 原生 Java Driver（底层原生API）

### 6\.1 三种方案核心定位总览

|开发方案|开发效率|查询性能|适配业务场景|核心优缺点|
|---|---|---|---|---|
|SDN Repository|极高（零SQL）|中等|简单单节点查询、基础关联查询、低复杂度高频查|不用手写Cypher，开发极速；复杂多属性筛选、深度遍历性能一般|
|Neo4jTemplate|中等|较高|**【你的核心场景首选】多属性企业节点、密集查询、中等复杂度筛选**|可手写精准Cypher、自动映射实体、规避原生解析冗余，兼顾效率与性能|
|原生Java Driver|极低（全手写）|最高|超大批量查询、复杂图遍历、多层关联、极致性能需求|性能拉满，但需要手动解析结果集、代码冗余、开发效率极低|

### 6\.2 针对性场景深度讲解（解决你的核心问题）

#### 你的业务现状

业务主体为**企业节点**，节点存储字段/属性极多，日常业务以**密集高频查询**为主，痛点：

1. 完全手写原生Driver代码：开发太慢、重复代码多、结果集解析繁琐、极易出错；

2. 纯用SDN Repository：多属性复杂筛选、批量查询存在性能冗余，无法自定义优化Cypher语句。

#### 最优选型结论（直接落地）

**主力方案：Neo4jTemplate \+ 少量SDN Repository**

90%企业多属性密集查询场景，使用 **Neo4jTemplate** 完美平衡「开发效率」和「查询性能」；简单单条件查询复用 Repository，无需重复造轮子。

### 6\.3 三种方案详细使用场景\+实操用法

#### 1、SpringDataNeo4j Repository（适合简单查询、快速开发）

**适用场景**：

- 企业节点**单条件、少条件查询**（如：根据企业ID、企业名称精准查询）；

- 基础新增、修改、删除节点/关系；

- 无复杂多属性组合筛选、无需自定义Cypher优化的场景。

**禁用场景**：企业节点10\+属性组合查询、批量分页查询、高频密集检索（框架自动生成的Cypher冗余，性能较差）。

**实操示例（极简高效）**

```java
// 无需写任何Cypher，框架自动生成语句
public interface EnterpriseRepo extends Neo4jRepository<Enterprise, Long> {
    // 单属性精准查询，适配高频简单检索
    Enterprise findByEntId(String entId);
    // 双属性组合简单查询
    List<Enterprise> findByEntTypeAndStatus(String entType, Integer status);
}

```

#### 2、Neo4jTemplate（你的核心场景最优解，重点掌握）

**适用场景（完全匹配你的业务）**：

- **企业节点属性极多**，需要多条件组合筛选、模糊查询、范围查询；

- **查询密集、高频访问**，需要手写优化版Cypher提升性能；

- 不想用原生Driver手动解析结果集，希望自动映射实体；

- 需要分页、排序、去重等复杂查询逻辑。

**核心优势**：

1. 支持**手写精准优化Cypher**，规避Repository自动生成语句的冗余问题，适配多属性复杂查询；

2. 自动将查询结果映射为Java实体，**不用像原生Driver那样手动遍历、赋值、封装数据**，大幅提升开发效率；

3. 兼容参数绑定、分页、排序，性能接近原生Driver，开发效率远超原生。

**实操示例（多属性企业高频查询）**

```java
@Autowired
private Neo4jTemplate neo4jTemplate;

/**
 * 多属性组合查询企业（适配属性多、查询密集场景）
 * 手写优化Cypher，性能可控，自动映射实体
 */
public List<Enterprise> findEnterpriseByCondition(String entType, Integer status, String keyword) {
    String cypher = "MATCH (e:Enterprise) " +
            "WHERE e.entType=$entType AND e.status=$status AND e.name CONTAINS $keyword " +
            "RETURN e ORDER BY e.createTime DESC";

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("entType", entType);
    paramMap.put("status", status);
    paramMap.put("keyword", keyword);

    // 核心：自动映射实体，无需手动解析结果集
    return neo4jTemplate.query(cypher, paramMap, Enterprise.class);
}

```

#### 3、原生Java Driver（极致性能场景专用，日常业务不推荐）

**适用场景**：

- 超大批量数据查询、批量导入、批量更新（上万条数据操作）；

- 多层深度关系遍历、复杂图算法查询（最短路径、关联链路挖掘）；

- 极致性能优化场景，需要精细控制事务、会话、查询逻辑。

**不适用你的日常查询业务**：

普通企业节点查询使用原生Driver，会产生大量冗余代码，需要手动获取Result、遍历Record、逐个set属性，**开发效率极低、极易出Bug，完全没必要**。

### 6\.4 最终落地选型方案（直接用于项目开发）

#### 1\. 简单高频单/双条件查询 → 【SDN Repository】

企业ID查询、状态查询、类型查询等基础接口，零代码开发，快速落地。

#### 2\. 多属性组合、密集复杂查询（主力业务）→ 【Neo4jTemplate】

所有企业多字段筛选、模糊查询、分页查询、排序查询，统一使用该方案，**兼顾性能与开发效率，完美解决你的痛点**。

#### 3\. 批量数据、复杂图算法、超大流量 → 【原生Driver】

仅用于特殊数据处理场景，日常业务查询禁止使用。

### 6\.5 高频查询优化小技巧（适配你的业务）

1. 企业高频筛选字段（类型、状态、名称），在Cypher中优先作为WHERE条件前置过滤，减少遍历数据量；

2. 多属性查询禁止使用Repository自动查询，必须手写优化Cypher，避免无效字段匹配；

3. 密集查询接口可添加本地缓存，减少数据库频繁访问压力。

### 6\.6 补充：Neo4j\-OGM 作用说明

OGM（对象图映射）是以上两种方案的底层核心，**无需手动操作**。它负责完成「数据库节点属性 ↔ Java实体类字段」的自动映射，适配3\.4\.19版本固定使用 **OGM3\.1\.x**，配套老版注解 `@NodeEntity、@RelationshipEntity`，支撑Template和Repository的实体自动封装能力。

### 1\. Neo4j\-OGM

对象图映射框架，**POJO 实体→自动映射节点 / 关系**，类似 MyBatis，SpringDataNeo4j 底层依赖，适配 3\.4 版本用 OGM3\.1\.x。

- 注解：`@NodeEntity(节点)、@RelationshipEntity(关系)、@StartNode/@EndNode`。

### 2\. SpringDataNeo4j（SDN）

Spring 官方封装，`Neo4jRepository`类似 JPA Repository，不用手写 Cypher，**你本地 3\.4\.19 固定搭配 Boot2\.1\.x\+SDN5\.x**。

### 3\. 原生 Java Driver

底层驱动，手动创建 Session 执行 Cypher，高性能批量插入场景使用，3\.4 固定驱动版本`1.7.5`。

## 七、SpringBoot 整合 Neo4j3\.4\.19 最简配置

### 7\.1 pom 关键依赖（Boot2\.1\.4\.RELEASE）

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-neo4j</artifactId>
    </dependency>
    <!--强制锁定驱动1.7.5适配3.4-->
    <dependency>
        <groupId>org.neo4j.driver</groupId>
        <artifactId>neo4j-java-driver</artifactId>
        <version>1.7.5</version>
    </dependency>
</dependencies>
```

### 7\.2 application\.yml 配置

```yaml
spring:
  data:
    neo4j:
      uri: bolt://127.0.0.1:7687
      username: neo4j
      password: 你的数据库密码
```

### 7\.3 实体 \& Repository 示例

```java
// 节点实体
@NodeEntity
@Data
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
}
// 持久层
public interface UserRepo extends Neo4jRepository<User,Long> {
    List<User> findByName(String name);
}
```

## 八、课后实操练习（小白必做）

1. 本地启动 Neo4j3\.4\.19，修改默认密码；

2. 使用 Cypher 创建 3 个用户节点：张三、李四、王五，创建关系：张三是李四好友、李四关注王五；

3. 编写 SpringBoot 项目，通过 SDN 实现用户新增、根据姓名查询接口；

4. 在 doc\.html（Knife4j）查看并调试接口。

## 九、拓展学习资源

1. Gitee 优先：搜索`springboot-neo4j3.4-demo`，国内适配 3\.4 的开源练手项目；

2. 官方示例：university 学院图谱项目（学生 / 课程 / 老师多关系经典案例）。

> （注：文档部分内容可能由 AI 生成）
