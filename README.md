<h1>Wallet Application</h1>
<div>Test task for https://leovegasgroup.com/</div>


<h3>Technologies stack:</h3>
<li>Spring boot</li>
<li>Spring data, Liquibase</li>
<li>H2 in memory db</li>
<li>Docker, Swagger, JUnit, JMeter</li>

<h3>At start from resources/users.csv will created users</h3>
<h3>For running app in docker, from project directory follow commands:</h3>
<li>./gradlew build</li>
<li>docker build -t wallet:latest .</li>
<li>docker run -p 8080:8080 wallet</li>

<h3>Postman</h3>
<li>postman/WalletApp...json</li>

<h3>Swagger</h3>
<li>swagger json path - swagger/openapi.json</li>
<li>openapi - http://localhost:8080/swagger-ui/index.html</li>

<h3>Load test with JMeter</h3>
<li>jmeter/leo-vegas.jmx</li>

<h3>How app can be improved or things i would like to do:</h3>
<div>Change DB for postgres. Add message queue so if our application will 
unexpectly crash, info about transaction will be storaged in queue.

In the real wallet application there will be asynchronous integration 
with the bank(by bank API) and each transaction will go through that bank. 
So i need to extend Transaction entity with for example TransactionStatus 
(NEW, ERROR, SUCCESS, etc) and update balance after receiving TransactionStatus.SUCCESS 
from bank(and create API for handle bank transaction-notifications). 
If we will face with big transaction streams i think we can configure loadbalancer 
and up few instances of application.</div>