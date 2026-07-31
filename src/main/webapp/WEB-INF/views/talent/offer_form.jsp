<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- 모달 스타일 -->
<style>
  .modal-overlay {
    display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(0,0,0,0.5); z-index: 9999; align-items: center; justify-content: center;
  }
  .modal-overlay.on { display: flex; }
  .form-modal {
    background: #fff; border-radius: 12px; width: 440px;
    padding: 24px; box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  }
</style>

<!-- ===== 함께하기 제의 모달 ===== -->
<div class="modal-overlay" id="offerModal" onclick="if(event.target===this)closeOffer()">
  <div class="modal form-modal" role="dialog" aria-modal="true">
    
    <div class="modal-head" style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
      <div class="mh-info">
          <h3 id="offerTitle" style="margin:0; font-size:18px;">함께하기 제의</h3>
          <div class="role" id="offerWho" style="font-size: 13px; color: var(--ink-soft); margin-top: 4px;"></div>
      </div>
      <button class="modal-close" onclick="closeOffer()" aria-label="닫기" style="background:none; border:none; cursor:pointer;">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    
    <div class="modal-body" id="offerBody">
      
      <!-- 💡 1. 입력 폼 영역 (초기화하기 쉽도록 form 태그 사용) -->
      <form id="offerForm">
          <div class="offer-proj" style="margin-bottom: 16px;">
            <div class="k" style="font-size: 13px; font-weight: 600; margin-bottom: 8px;">초대할 프로젝트</div>
            <select id="offerProj" style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);">
              <option>AI 헬스케어 웹서비스 (팀장)</option>
              <option>데이터베이스 텀 프로젝트</option>
            </select>
          </div>
          
          <div class="fld one" style="margin-bottom: 16px;">
              <label style="font-size: 13px; font-weight: 600; margin-bottom: 8px; display: block;">맡아줬으면 하는 역할</label>
              <input type="text" id="offerRole" placeholder="예: 백엔드 · 인증/권한" style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);">
          </div>
          
          <div class="fld one">
              <label style="font-size: 13px; font-weight: 600; margin-bottom: 8px; display: block;">제의 메시지</label>
              <textarea placeholder="왜 함께하고 싶은지, 어떤 점이 좋았는지 적어주세요." style="min-height:90px; width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);"></textarea>
          </div>
          
          <div class="form-foot" style="margin-top: 24px; display:flex; justify-content:flex-end; gap:8px;">
            <button type="button" class="btn ghost" onclick="closeOffer()">취소</button>
            <button type="button" class="btn pri" onclick="sendOffer()">제의 보내기</button>
          </div>
      </form>

      <!-- 💡 2. 성공 메시지 영역 (처음엔 display: none으로 숨김) -->
      <div id="offerSuccess" class="submit-success" style="display: none; text-align:center; padding: 20px 0;">
        <div style="color: #2b46c8; margin-bottom:16px;">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        </div>
        <h3 style="margin-bottom:12px; font-size:20px;">제의를 보냈어요!</h3>
        <p id="offerSuccessText" style="color:var(--ink-soft); font-size:14px; line-height:1.6; margin-bottom:24px;">
            <!-- JS에서 이름이 동적으로 들어갑니다 -->
        </p>
        <button type="button" class="btn pri" style="width:100%; justify-content:center; padding:12px;" onclick="closeOffer()">확인</button>
      </div>

    </div>
  </div>
</div>