<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="woopaca.jspcafe.servlet.dto.MembersResponse" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" as="style" crossorigin
        href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/variable/pretendardvariable.min.css" />
  <link rel="stylesheet" href="../css/style.css">
  <link rel="stylesheet" href="../css/main.css">
  <title>찬우 카페</title>
</head>
<body>
<div id="container">
  <div id="header">
    <a id="greeting" href="/">찬우 카페</a>
    <a id="login-button" href="/user/login.html">로그인/회원가입</a>
  </div>
  <div id="posts-container">
    <div id="posts-information">전체 멤버 <%= request.getAttribute("membersCount") %>명</div> <!-- 전체 회원 수 -->
    <div id="posts">
      <div id="posts-header">
        <div class="post-element">닉네임</div>
        <div class="post-element">이메일</div>
        <div class="post-element">회원가입일</div>
      </div>
      <%
        List<MembersResponse> members = (List<MembersResponse>) request.getAttribute("members");
        for (MembersResponse member : members) {
      %>
      <a class="post" href="/users/<%= member.id() %>">
        <div class="post-element" style="font-size: 1.1em; font-weight: 600;">
          <%= member.nickname() %>
        </div>
        <div class="post-element">
          <%= member.email() %>
        </div>
        <div class="post-element">
          <%= member.createdAt() %>
        </div>
      </a>
      <% } %>
    </div>
  </div>
</div>
</body>
</html>
