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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.neo4j.ogm.annotation.Relationship

/*
以下是在图上英文下方添加对应中文翻译后的内容：

- Department（院系 ）
- CURRICULUM（课程体系 ）
- Subject（学科/科目 ）
- TAUGHT_BY（由……教授 ）
- Teacher（教师 ）
- TEACHES_CLASS（教授班级 ）
- Class（班级 ）
- ENROLLED（已注册/已选课 ）
- Student（学生 ）
- BUDDY（伙伴/学伴 ）
- StudyBuddy（学习伙伴 ）
- COURSE（课程 ）
 */
class Subject extends Entity {

    @JsonProperty("name")
    String name

    /**
     * （Department 院系）-[CURRICULUM 课程体系 ]->(Subject 科目)
     */
    @JsonIgnore
    @Relationship(type = "CURRICULUM", direction = Relationship.INCOMING)
    Department department

    /**
     * (Subject 科目)-[TAUGHT_BY 由……教授 ]->(Teacher教师)
     */
    @Relationship(type = "TAUGHT_BY")
    Set<Teacher> teachers

    @Relationship(type = "SUBJECT_TAUGHT", direction = "INCOMING")
    Set<Course> courses

    Subject(String name) {
        this()
        this.name = name
    }

    Subject() {
        this.teachers = new HashSet<>()
        this.courses = new HashSet<>()
    }

    @Override
    String toString() {
        return "Subject{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", department=" + department +
                ", teachers=" + teachers.size() +
                '}'
    }
}
