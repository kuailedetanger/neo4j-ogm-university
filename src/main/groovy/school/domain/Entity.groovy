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

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.voodoodyne.jackson.jsog.JSOGGenerator

/*


由于每个实体都需要一个ID，我们将创建一个 Entity 超类。



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
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIdentityInfo(generator = JSOGGenerator.class)
abstract class Entity {

    @JsonProperty("id")
    private Long id

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || id == null || getClass() != o.getClass()) return false

        Entity entity = (Entity) o

        if (!id.equals(entity.id)) return false

        return true
    }

    @Override
    int hashCode() {
        return (id == null) ? -1 : id.hashCode()
    }
}
