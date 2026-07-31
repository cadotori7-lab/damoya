<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="nav" value="admin" scope="request"/>

<c:choose>
  <c:when test="${dashboard.connected and dashboard.clusterStatus eq 'green'}">
    <c:set var="serviceClass" value="healthy"/>
    <c:set var="serviceLabel" value="정상"/>
  </c:when>
  <c:when test="${dashboard.connected and dashboard.clusterStatus eq 'yellow'}">
    <c:set var="serviceClass" value="warning"/>
    <c:set var="serviceLabel" value="점검 필요"/>
  </c:when>
  <c:when test="${dashboard.connected}">
    <c:set var="serviceClass" value="danger"/>
    <c:set var="serviceLabel" value="주의"/>
  </c:when>
  <c:otherwise>
    <c:set var="serviceClass" value="offline"/>
    <c:set var="serviceLabel" value="연결 끊김"/>
  </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>관리자 대시보드 | 다모여</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
  <style>
    #v-admin{animation:fade .35s ease}
    .admin-heading{display:flex;align-items:flex-start;justify-content:space-between;gap:24px;margin-bottom:24px}
    .admin-heading .sub{margin-bottom:0}
    .admin-heading-side{display:flex;flex-direction:column;align-items:flex-end;gap:9px;flex:none}
    .service-badge{display:inline-flex;align-items:center;gap:8px;padding:7px 11px;border-radius:999px;font-family:var(--mono);font-size:11px;font-weight:700}
    .service-badge::before{content:"";width:7px;height:7px;border-radius:50%;background:currentColor;box-shadow:0 0 0 4px rgba(18,146,95,.1)}
    .service-badge.healthy{color:var(--ok);background:var(--ok-bg)}
    .service-badge.warning{color:var(--wait);background:var(--wait-bg)}
    .service-badge.danger{color:var(--reject);background:var(--reject-bg)}
    .service-badge.offline{color:var(--grey);background:var(--grey-bg)}
    .refreshed-at{font-family:var(--mono);font-size:10.5px;color:var(--ink-soft)}

    .dashboard-alert{display:flex;align-items:flex-start;gap:12px;margin-bottom:20px;padding:15px 17px;border:1px solid #f1c6cf;border-radius:12px;background:var(--reject-bg);color:#8e2f41}
    .dashboard-alert .alert-mark{width:24px;height:24px;border-radius:50%;display:grid;place-items:center;background:#fff;font-weight:800;flex:none}
    .dashboard-alert strong{display:block;font-size:14px;margin-bottom:2px}
    .dashboard-alert p{font-size:13px;line-height:1.55}

    #v-admin .stats{margin-bottom:24px}
    #v-admin .stat{display:block;position:relative;overflow:hidden;min-height:150px;transition:transform .16s,border-color .16s,box-shadow .16s}
    #v-admin a.stat:hover{transform:translateY(-2px);border-color:var(--line-strong);box-shadow:0 12px 26px -18px rgba(20,35,63,.38)}
    #v-admin .stat::before{content:"";position:absolute;left:0;right:0;top:0;height:3px;background:var(--stat-color,var(--accent))}
    #v-admin .stat .n{font-family:var(--mono);font-feature-settings:"tnum"}
    #v-admin .stat .d{margin-top:8px;color:var(--ink-soft);font-family:var(--sans);font-weight:500;line-height:1.4}
    #v-admin .stat .go{position:absolute;right:18px;bottom:17px;font-size:18px;color:var(--ink-soft);transition:transform .16s,color .16s}
    #v-admin a.stat:hover .go{transform:translateX(3px);color:var(--accent)}

    .dashboard-grid{display:grid;grid-template-columns:minmax(0,1.3fr) minmax(280px,.7fr);gap:18px;align-items:start}
    .dashboard-panel{background:var(--surface);border:1px solid var(--line);border-radius:var(--r);overflow:hidden}
    .panel-head{display:flex;align-items:center;justify-content:space-between;gap:14px;padding:18px 20px;border-bottom:1px solid var(--line)}
    .panel-head h2{font-size:16px;font-weight:800}
    .panel-head p{margin-top:2px;color:var(--ink-soft);font-size:12.5px}
    .panel-body{padding:8px}

    .admin-menu{display:grid;grid-template-columns:1fr 1fr;gap:8px}
    .admin-menu-item{display:flex;flex-direction:column;min-height:178px;padding:19px;border:1px solid transparent;border-radius:11px;background:var(--paper);transition:background .16s,border-color .16s,transform .16s}
    .admin-menu-item:hover{background:var(--surface);border-color:var(--line-strong);transform:translateY(-1px)}
    .menu-kicker{font-family:var(--mono);font-size:10.5px;font-weight:700;letter-spacing:.07em;color:var(--accent);text-transform:uppercase}
    .admin-menu-item h3{font-size:17px;font-weight:800;margin:10px 0 5px}
    .admin-menu-item p{font-size:13px;color:var(--ink-soft);line-height:1.6}
    .menu-link{display:flex;align-items:center;justify-content:space-between;margin-top:auto;padding-top:18px;font-size:13px;font-weight:700;color:var(--accent)}

    .system-list{padding:4px 18px 2px}
    .system-row{display:flex;align-items:center;justify-content:space-between;gap:18px;padding:13px 2px;border-bottom:1px solid var(--line)}
    .system-row:last-child{border-bottom:0}
    .system-row dt{font-size:13px;color:var(--ink-soft)}
    .system-row dd{font-family:var(--mono);font-size:12px;font-weight:700;text-align:right}
    .system-row dd.status-${serviceClass}{color:var(--ok)}
    .system-row dd.status-warning{color:var(--wait)}
    .system-row dd.status-danger{color:var(--reject)}
    .system-row dd.status-offline{color:var(--grey)}
    .panel-foot{padding:14px 18px;border-top:1px solid var(--line);background:var(--paper)}
    .refresh-btn{width:100%;height:40px;justify-content:center}
    .refresh-btn:disabled{opacity:.6;cursor:wait}

    @media(max-width:900px){
      #v-admin .stats{grid-template-columns:repeat(2,1fr)}
      .dashboard-grid{grid-template-columns:1fr}
    }
    @media(max-width:640px){
      .admin-heading{flex-direction:column}
      .admin-heading-side{align-items:flex-start}
      #v-admin .stats,.admin-menu{grid-template-columns:1fr}
      #v-admin .stat{min-height:132px}
    }
  </style>
</head>
<body>
  <jsp:include page="../includes/header.jsp"/>

  <main>
    <section id="v-admin" aria-labelledby="dashboard-title">
      <div class="admin-heading">
        <div>
          <div class="eyebrow">Admin console</div>
          <h1 class="page" id="dashboard-title"><em>관리자 대시보드</em></h1>
          <p class="sub">서비스 현황을 확인하고 운영 메뉴로 빠르게 이동하세요.</p>
        </div>
        <div class="admin-heading-side">
          <span class="service-badge ${serviceClass}">
            검색 서비스 ${serviceLabel}
          </span>
          <span class="refreshed-at" id="refreshedAt">집계 시각 확인 중</span>
        </div>
      </div>

      <c:if test="${not dashboard.connected}">
        <div class="dashboard-alert" role="alert">
          <span class="alert-mark" aria-hidden="true">!</span>
          <div>
            <strong>실시간 통계를 불러오지 못했습니다.</strong>
            <p><c:out value="${dashboard.errorMessage}"/></p>
          </div>
        </div>
      </c:if>

      <div class="stats" aria-label="서비스 주요 통계">
        <a class="stat" href="${ctx}/admin/accounts" style="--stat-color:var(--accent)">
          <div class="k">전체 회원</div>
          <div class="n"><fmt:formatNumber value="${dashboard.memberCount}"/><small> 명</small></div>
          <div class="d">검색 인덱스에 등록된 회원</div>
          <span class="go" aria-hidden="true">→</span>
        </a>
        <a class="stat" href="${ctx}/project/list" style="--stat-color:var(--ok)">
          <div class="k">전체 프로젝트</div>
          <div class="n"><fmt:formatNumber value="${dashboard.projectCount}"/><small> 개</small></div>
          <div class="d">검색 인덱스에 등록된 프로젝트</div>
          <span class="go" aria-hidden="true">→</span>
        </a>
        <a class="stat" href="${ctx}/admin/posts" style="--stat-color:var(--reject)">
          <div class="k">누적 신고</div>
          <div class="n"><fmt:formatNumber value="${dashboard.reportCount}"/><small> 건</small></div>
          <div class="d">확인이 필요한 신고 문서</div>
          <span class="go" aria-hidden="true">→</span>
        </a>
        <div class="stat" style="--stat-color:var(--wait)">
          <div class="k">전체 검색 문서</div>
          <div class="n"><fmt:formatNumber value="${dashboard.totalDocumentCount}"/><small> 건</small></div>
          <div class="d">회원·프로젝트·신고 문서 합계</div>
        </div>
      </div>

      <div class="dashboard-grid">
        <section class="dashboard-panel" aria-labelledby="management-title">
          <div class="panel-head">
            <div>
              <h2 id="management-title">운영 관리</h2>
              <p>관리할 항목을 선택하세요.</p>
            </div>
          </div>
          <div class="panel-body">
            <div class="admin-menu">
              <a class="admin-menu-item" href="${ctx}/admin/accounts">
                <span class="menu-kicker">Accounts</span>
                <h3>계정 관리</h3>
                <p>회원 계정을 조회하고 역할과 이용 상태를 확인합니다.</p>
                <span class="menu-link">계정 관리 열기 <span aria-hidden="true">→</span></span>
              </a>
              <a class="admin-menu-item" href="${ctx}/admin/posts">
                <span class="menu-kicker">Contents</span>
                <h3>게시물 관리</h3>
                <p>프로젝트 게시물과 신고된 콘텐츠의 처리 상태를 확인합니다.</p>
                <span class="menu-link">게시물 관리 열기 <span aria-hidden="true">→</span></span>
              </a>
            </div>
          </div>
        </section>

        <aside class="dashboard-panel" aria-labelledby="system-title">
          <div class="panel-head">
            <div>
              <h2 id="system-title">검색 서비스 상태</h2>
              <p>Elasticsearch 집계 기준</p>
            </div>
          </div>
          <dl class="system-list">
            <div class="system-row">
              <dt>연결 상태</dt>
              <dd class="status-${serviceClass}">${dashboard.connected ? 'CONNECTED' : 'OFFLINE'}</dd>
            </div>
            <div class="system-row">
              <dt>클러스터 상태</dt>
              <dd><c:out value="${dashboard.clusterStatus}"/></dd>
            </div>
            <div class="system-row">
              <dt>회원 문서</dt>
              <dd><fmt:formatNumber value="${dashboard.memberCount}"/></dd>
            </div>
            <div class="system-row">
              <dt>프로젝트 문서</dt>
              <dd><fmt:formatNumber value="${dashboard.projectCount}"/></dd>
            </div>
            <div class="system-row">
              <dt>신고 문서</dt>
              <dd><fmt:formatNumber value="${dashboard.reportCount}"/></dd>
            </div>
          </dl>
          <div class="panel-foot">
            <button class="btn ghost refresh-btn" type="button" id="refreshDashboard">통계 새로고침</button>
          </div>
        </aside>
      </div>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp"/>
  <script>
    (function () {
      var refreshedAt = document.getElementById('refreshedAt');
      var refreshButton = document.getElementById('refreshDashboard');

      if (refreshedAt) {
        refreshedAt.textContent = '집계 ' + new Intl.DateTimeFormat('ko-KR', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).format(new Date());
      }

      if (refreshButton) {
        refreshButton.addEventListener('click', function () {
          refreshButton.disabled = true;
          refreshButton.textContent = '새로고침 중…';
          window.location.reload();
        });
      }
    }());
  </script>
</body>
</html>
