# 실습2: Microservice Monitoring with Service Discovery (Eureka) and Spring Boot Admin

[2019.06.15] Oracle Developer Meetup Hands-On 문서

***

### 실습 목표

Spring Boot 마이크로 서비스를 Service Discovery (Eureka)와 Spring Boot Admin을 사용하여 Metrics 모니터링

***

### Spring Cloud Netflix Eureka 서버 (Spring Boot 프로젝트) 생성 및 구성
spring-admin-server와 마찬가지로 제공된 Eclipse Spring Tool Suite(이하 STS) 프로젝트중에서 eureka-discovery-server가 Service Discovery Server 역할을 합니다.

spring-admin-server와 마찬가지로 제공되는 프로젝트를 활용해도 되지만, 새로 만들어볼 수 있습니다. 새로 프로젝트를 생성해서 Eureka Discovery Server를 구성할 경우 아래 **Eureka Discovery 서버 만들기**를 클릭해서 가이드데로 진행합니다.

<details>
<summary>Eureka Discovery 서버 만들기</summary>
<div markdown="1">

1. STS에서 File > New > Spring Starter Project를 선택 (안보일 경우 Other 클릭 후 검색)
<img src="images/sts-create-spring-starter-project.png" width="80%">

2. Name과 Java Version을 다음과 같이 입력 후 Next > Finish 클릭
* Name
    * eureka-discovery-server-2
* Java Version 
    * 12

3. STS에서 pom.xml에 Dependency 설정을 합니다. 좌측 STS Package Explorer에서 **ureka-discovery-server-2/pom.xml** 파일을 더블 클릭하고 다음과 같이 기존 설정된 내용에 아래 properties, dependencies, dependencyManagement까지의 내용으로 변경하고 저장(Ctrl + s)합니다.
    > Eclipse STS에서 XML에 대한 자동 포맷팅 단축키는 ***'Ctrl + Shift + f'*** 입니다.

    ```xml
    <properties>
        <java.version>12</java.version>
        <spring-cloud.version>Greenwich.RELEASE</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>javax.activation</groupId>
            <artifactId>activation</artifactId>
            <version>1.1.1</version>
        </dependency>

        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.0</version>
        </dependency>

        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-core</artifactId>
            <version>2.3.0</version>
        </dependency>

        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-impl</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```

4. STS에서 Spring Boot Application 파일(eureka-discovery-server-2/src/main/java/com/example/demo/EurekaDiscoveryServer2Application.java)을 열고 다음과 같이 @EnableEurekaServer 을 추가합니다.

    > STS에서 자동 Package Import 단축키는 ***'Ctrl + Shift + o'***입니다. Annotation을 추가하고 위 단축키를 눌러서 관련 패키지를 임포트 합니다.

    ```java
    @SpringBootApplication
    @EnableEurekaServer
    public class EurekaDiscoveryServerApplication {

        public static void main(String[] args) {
            SpringApplication.run(EurekaDiscoveryServerApplication.class, args);
        }
    }
    ```

5. STS에서 Spring Boot Properties(eureka-discovery-server-2/src/main/resources/application.properties) 파일에 다음과 같이 추가합니다. 

    ```properties
    spring.application.name=eureka-discovery-server 
    server.port=8761

    # Logging
    logging.level.org.springframework=INFO
    logging.file=./logs/spring-boot-logging.log

    # Eureka
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false
    ```

6. Spring Boot Admin Server를 시작합니다. Windows Command 또는 Terminal을 열고 다음과 같이 실행합니다.

    ```
    $ cd {ROOT}/sts_workspace/eureka-discovery-server-2

    $ mvn spring-boot:run
    ```

7. Browser에서 http://localhost:8761으로 Eureka Server Console에 접속합니다.  
    <img src="images/eureka-admin-console.png" width="100%">

    > 서버 종료는 ***Ctrl + c***로 종료합니다.
</div>
</summary>
</details>

### Spring Admin Server 구성
첫 번째 실습에서는 모든 서비스들을 직접 Spring Admin Server에 등록해 연결했습니다. 이번 실습은 모든 서비스들이 Eureka Discovery Server의 Registry에 등록이 되고, Spring Boot Admin이 Eureka를 통해서 서비스들에 대한 Metrics 정보를 얻습니다.

* **실습 1**
    * Love Calculator 서비스 -> Spring Boot Admin (모니터링 서버)

* **실습 2**
    * Love Calculator 서비스 -> Eureka (Discovery 서버) <- Spring Boot Admin (모니터링 서버)

따라서 서비스와 Spring Boot Admin을 Eureka Client로 등록해서 연결해줘야 합니다.

1. STS Package Explorer에서 spring-admin-server/pom.xml을 열고 spring-cloud-starter-netflix-eureka-client Dependency를 추가합니다.

    ```xml
    ...

        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    ```

2. STS에서 Spring Boot Application 파일(spring-admin-server/src/main/java/com/example/demo/SpringAdminServerApplication.java)을 열고 다음과 같이 @EnableDiscoveryClient 을 추가합니다.

```
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class SpringAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAdminServerApplication.class, args);
	}

	@Configuration
	public class SecurityConfig extends WebSecurityConfigurerAdapter {
```












***

* [실습3: Microservice Monitoring with Prometheus and Grafana](HOL-PART3.md)

* [첫 페이지로 가기](README.md)

***

### 참고
본 실습 관련 좀 더 상세한 내용은 아래 블로그 참고하세요.
* https://mangdan.github.io/spring-boot-microservice-monitoring-1/