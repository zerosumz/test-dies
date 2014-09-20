package com.aperturesoft.repo;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.aperturesoft.BirthDay;

public interface TestMapper {

	@Select("SELECT * FROM FOO WHERE ID = #{id} ")
	BirthDay selectMe(@Param("id") int id);
}
