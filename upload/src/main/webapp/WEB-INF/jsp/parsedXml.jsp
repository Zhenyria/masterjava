<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>XML file parsing result</title>
</head>
<body>
<div>Parsed XML</div>
<div>Projects</div>
<%--@elvariable id="projects" type="java.util.List<ru.javaops.masterjava.upload.service.xml.schema.Project>"--%>
<c:forEach var="project" items="${projects}">
    <div>${project.name}</div>
    <div>${project.description}</div>
    <div>Groups</div>
    <div>
        <c:if test="${project.group.size() gt 0}">
            <table>
                <c:forEach var="group" items="${project.group}">
                    <tr>
                        <td>${group.name}</td>
                        <td>${group.type}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>
</c:forEach>
</body>
</html>
