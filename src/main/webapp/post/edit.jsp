<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post-form.css">
  <title>찬우 카페</title>
</head>
<body>
<div id="container">
  <jsp:include page="${pageContext.request.contextPath}/common/header.jsp" />
  <form id="post-form" action="${pageContext.request.contextPath}/posts/${post.id()}">
    <div id="form-title">
      글 수정
    </div>
    <div class="form-element">
      <div class="form-label">제목</div>
      <input type="text" autofocus required minlength="2" maxlength="30" id="title" name="title"
             placeholder="글의 제목을 입력해주세요" value="<c:out value="${post.title()}" />">
    </div>
    <div class="form-element">
      <div class="form-label">내용</div>
      <textarea name="content" required minlength="2" maxlength="1000" id="content"
                placeholder="글의 내용을 입력해주세요"><c:out value="${post.content()}" /></textarea>
    </div>
    <div id="post-submit" onclick="handleSubmit()">수정 완료!</div>
  </form>
</div>
<script>
  document.getElementById('post-form').removeEventListener('submit', (e) => e.preventDefault());

  const handleSubmit = async () => {
    const form = document.getElementById('post-form');
    const formData = new URLSearchParams(new FormData(form)).toString();

    let action = form.action;
    console.log('action: ', action);
    let response = await fetch(action, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: formData,
    });

    if (response.ok) {
      window.location.href = '/posts/' + ${post.id()};
    } else {
      let body = await response.text();
      document.open();
      document.write(body);
      document.close();
    }
  };
</script>
</body>
</html>
