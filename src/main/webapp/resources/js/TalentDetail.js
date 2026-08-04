function deleteTalent(postId) {
          if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
              location.href = ctx + "/talent/delete?id=" + postId;
          }
      }
      
//비로그인 유저가 접근할 때 호출되는 함수
function requireLogin() {
alert("로그인 후 사용할 수 있어요!");
location.href = ctx + "/auth/login";
}