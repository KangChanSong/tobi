package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.tobi.domain.sql.jaxb.SqlMap;
import com.tobi.domain.sql.jaxb.SqlType;

public class JaxbTest {

	@Test
	public void readSqlmap() throws JAXBException {
		String contextPath = SqlMap.class.getPackage().getName();
		// 바인딩용 클래스들 위치를 가지고 JAXB 컨텍스트를 만듦
		JAXBContext context = JAXBContext.newInstance(contextPath);
		//언마샬러 생성
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		//언마샬을 하면 매핑된 오브젝트 트리의 루트인 SqlMap 을 돌려준다.
		SqlMap sqlMap = (SqlMap) unmarshaller.unmarshal(
				getClass().getResourceAsStream("sqlmap.xml"));
		
		List<SqlType> sqlList = sqlMap.getSql();
		
		assertThat(sqlList.size(), is(3));
		assertThat(sqlList.get(0).getKey(), is("add"));
		assertThat(sqlList.get(0).getValue(), is("insert"));
		assertThat(sqlList.get(1).getKey(), is("get"));
		assertThat(sqlList.get(1).getValue(), is("select"));
		assertThat(sqlList.get(2).getKey(), is("delete") );
		assertThat(sqlList.get(2).getValue(), is("delete") );
	}
}
