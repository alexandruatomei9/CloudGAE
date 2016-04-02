<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Hello App Engine</title>
  </head>

  <body>
    <h1>Hello App Engine!</h1>
	<p>Hello, ${userName}</p>
    <a href="<c:url value="${logoutUrl}"/>">Logout</a>
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
    <a href="calendar">Get Calendar</a>
    <jsp:include page="${request.contextPath}/calendar"></jsp:include>
  </body>
</html>