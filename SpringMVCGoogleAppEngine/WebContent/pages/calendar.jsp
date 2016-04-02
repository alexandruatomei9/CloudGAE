<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<body>
	<h1>GAE + Spring 3 MVC REST example</h1>

	<c:forEach var="event" items="${events}">
		<c:out value="${event}" />
	</c:forEach>
</body>
</html>