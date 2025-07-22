/*
 * Copyright [2011-2016] "Neo Technology"
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package school.domain

import com.fasterxml.jackson.annotation.JsonProperty
import org.neo4j.ogm.annotation.Relationship

/**
 * Our college will contain departments 系
 *
 */
class Department extends Entity {

    @JsonProperty("name")
    String name

    /**
     * 每个系的老师
     */
    @Relationship(type = "DEPARTMENT_MEMBER")
    Set<Teacher> teachers

    /**
     * 一位teacher教授的各种 学科
     * @Relationship 注解 默认情况下，方向假定为 OUTGOING: (Department 院系 )-[CURRICULUM 课程体系 ]->(Subject 学科/科目)
     *
     *
     */
    @Relationship(type = "CURRICULUM")
    Set<Subject> subjects

    /**
     * Neo4j-OGM 还需要一个公共的无参数构造函数，以便能够从我们所有带注释的实体构造对象。我们将确保所有实体都有这样一个构造函数。
     */
    Department() {
        this.teachers = new HashSet<>()
        this.subjects = new HashSet<>()
    }

    Department(String name) {
        this()
        this.name = name
    }

    @Override
    String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teachers=" + teachers.size() +
                ", subjects=" + subjects.size() +
                '}'
    }
}
