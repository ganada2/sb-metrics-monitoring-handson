package com.kakao.globalid.guid.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class GuidDBInitializer implements ApplicationRunner {
        
        @Autowired
	    JdbcTemplate jdbcTemplate;

        @Override
	    public void run(ApplicationArguments args) throws Exception {
        
            try{
                checkDatabaseConnection();

                // Global ID 용 테이블 생성
                StringBuilder sb = new StringBuilder();

                /* 테이블 생성(globalid)  */
                sb.append( "CREATE TABLE IF NOT EXISTS globalid( " )
                .append("CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP(3), ")
                .append("UPDATE_PROGRESS VARCHAR(1) DEFAULT 'C', ")
                .append("GUID varchar(30) primary key, ")
                //.append("GUID varchar(30), ")
                .append("WORK_LAYER varchar(2) not null );");
	            String sql1 = sb.toString();
                System.out.println(sql1);

                jdbcTemplate.execute(sql1);
                sb.setLength(0);

	            /* guid sequence 생성 ( globalid_seq ) 1~9,999,999,999 */ 
                sb.append("CREATE SEQUENCE globalid_seq ")
                .append("START WITH 1 ")
                .append("INCREMENT BY 1 ")
                .append("MINVALUE 1 ")
                .append("MAXVALUE 9999999999 ")
                .append("CYCLE;" );
                //.append("CACHE 30000 ;");
                String sql2 = sb.toString();
                System.out.println(sql2);

                jdbcTemplate.execute(sql2);
                sb.setLength(0);

	            /* serial sequence 생성 ( serial_seq ) 1~9,999 */ 
                sb.append("CREATE SEQUENCE serial_seq ")
                .append("START WITH 1 ")
                .append("INCREMENT BY 1 ")
                .append("MINVALUE 1 ")
                .append("MAXVALUE 9999 ")
                .append("CYCLE ")
                .append("CACHE 9999 ;");
                String sql3 = sb.toString();

                jdbcTemplate.execute(sql3);
                sb.setLength(0);

	            /* 기준일자(comm_date) 테이블 생성 */ 
                sb.append("create table comm_date(")
                .append("before_date varchar(6) not null, ")
                .append("cur_date varchar(6) not null, ")
                .append("after_date varchar(6) not null, ")
                .append("UPDATE_PROGRESS VARCHAR(1) DEFAULT 'C', ")
                .append("last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP(3) );");
                String sql4 = sb.toString();

                jdbcTemplate.execute(sql4);
                sb.setLength(0);

                /* 기준일자 테이블 입력( 어제, 오늘, 내일) */
                sb.append("INSERT INTO comm_date (before_date, cur_date, after_date) ")
                .append("SELECT FORMATDATETIME(CURRENT_DATE - 1, 'yyMMdd') AS before_date, ")
                .append("FORMATDATETIME(CURRENT_DATE, 'yyMMdd') AS cur_date, ")
                .append("FORMATDATETIME(CURRENT_DATE + 1, 'yyMMdd') AS after_date; ");  
	            String sql5 = sb.toString();

                jdbcTemplate.execute(sql5);
                sb.setLength(0);
                
                /* 테이블 생성(globalid_ext)  */
                sb.append( "CREATE TABLE IF NOT EXISTS globalid_ext( " )
                .append("UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP(3), ")
                //.append("SEQUENCE INT DEFAULT globalid_seq.NEXTVAL PRIMARY KEY,")
                .append("SEQUENCE INT DEFAULT globalid_seq.NEXTVAL,")
                .append("GUID varchar(30) not null, ")
                .append("WORK_LAYER varchar(2) not null ); ");
	            String sql6 = sb.toString();
                System.out.println(sql6);

                jdbcTemplate.execute(sql6);
                sb.setLength(0);

            System.out.println("### DataBase Initialize Success!! ###");
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new Exception("### DataBase Initialize fail!! ###");
            }

	     //   jdbcTemplate.execute("insert into global_id values (globalid_seq.currval,'init', 'test', globalid_seq.currval)");
	    }

        public void checkDatabaseConnection() throws Exception {
            try {
                // 연결이 성공했을 때
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("### DataBase Connection fail!! ###");
            }
        }

}
