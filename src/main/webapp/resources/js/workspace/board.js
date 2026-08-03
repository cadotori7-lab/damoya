// DB 업무 데이터를 JSP의 숨겨진 요소에서 읽는다.
const TASKS = Array.from(
  document.querySelectorAll("#taskData .task-source")
).map(element => ({
  id: Number(element.dataset.taskId),
  status: element.dataset.status,
  assigneeId: element.dataset.assigneeId,
  name: element.querySelector(".task-name").textContent.trim(),
  description: element
    .querySelector(".task-description")
    .textContent
    .trim(),
  dueDate: element
    .querySelector(".task-due-date")
    .textContent
    .trim(),
  submitTitle: element
    .querySelector(".task-submit-title")
    .textContent
    .trim(),
  submitContent: element
    .querySelector(".task-submit-content")
    .textContent
    .trim(),
  submitFile: element
    .querySelector(".task-submit-file")
    .textContent
    .trim(),
  rejectReason: element
    .querySelector(".task-reject-reason")
    .textContent
    .trim()
}));

const TEAM_MEMBERS = Array.from(
  document.querySelectorAll("#teamMemberData .member-source")
).map(element => ({
  id: Number(element.dataset.memberId),
  projectRole: element.dataset.projectRole,
  name: element.querySelector(".member-name").textContent.trim(),
  major: element.querySelector(".member-major").textContent.trim()
}));

const COLUMNS = [
  {
    status: "ONGOING",
    label: "진행중",
    color: "var(--accent)",
    chip: "ing"
  },
  {
    status: "REVIEW",
    label: "검수 대기",
    color: "var(--wait)",
    chip: "wait"
  },
  {
    status: "REJECTED",
    label: "반려",
    color: "var(--reject)",
    chip: "reject"
  },
  {
    status: "APPROVED",
    label: "승인 완료",
    color: "var(--ok)",
    chip: "approve"
  }
];

const STATUS_LABEL = {
  ONGOING: "진행중",
  REVIEW: "검수 대기",
  REJECTED: "반려",
  APPROVED: "승인 완료"
};

const STATUS_CHIP = {
  ONGOING: "ing",
  REVIEW: "wait",
  REJECTED: "reject",
  APPROVED: "approve"
};

const MEMBER_COLORS = [
  "#2b46c8",
  "#0f9d8c",
  "#8256e0",
  "#c98a12",
  "#d1435b",
  "#2878b5"
];

let taskSearch = "";
let activeTaskView = "all";
const IS_LEADER =
  String(window.IS_LEADER).toLowerCase() === "true";

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatDueDate(date) {
  if (!date) {
    return "미정";
  }

  const parts = date.split("-");

  if (parts.length !== 3) {
    return date;
  }

  return `${parts[1]}.${parts[2]}`;
}

function getOriginalFileName(savedFileName) {
  if (!savedFileName) {
    return "";
  }

  // 서버 저장명 형식: UUID_원본파일명
  return savedFileName.replace(
    /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}_/i,
    ""
  );
}

function getMemberColor(memberId) {
  const colorIndex = Math.abs(Number(memberId) || 0)
    % MEMBER_COLORS.length;

  return MEMBER_COLORS[colorIndex];
}

function getMemberInitial(name) {
  return Array.from(String(name || "?").trim())[0] || "?";
}

function getMemberRoleLabel(member) {
  const role = member.projectRole === "LEADER"
    ? "팀장"
    : "팀원";

  return member.major
    ? `${role} · ${member.major}`
    : role;
}

function getAssigneeLabel(task) {
  if (!task.assigneeId) {
    return "담당자 미정";
  }

  const assignee = TEAM_MEMBERS.find(
    member => member.id === Number(task.assigneeId)
  );

  return assignee && assignee.name
    ? `${assignee.name}#${task.assigneeId}`
    : `담당자 #${task.assigneeId}`;
}

function isDueSoon(date) {
  if (!date) {
    return false;
  }

  const today = new Date();
  const dueDate = new Date(`${date}T23:59:59`);
  const difference = dueDate.getTime() - today.getTime();
  const remainingDays = difference / (1000 * 60 * 60 * 24);

  return remainingDays >= 0 && remainingDays <= 3;
}

function matchesSearch(task, keyword) {
  if (!keyword) {
    return true;
  }

  const target = [
    task.name,
    task.description,
    task.assigneeId
  ].join(" ").toLowerCase();

  return target.includes(keyword);
}

function renderKanban() {
  const keyword = taskSearch.trim().toLowerCase();

  const filteredTasks = TASKS.filter(task => {
    const isMine =
      Number(task.assigneeId) === Number(window.LOGIN_MEMBER_ID);

    const matchesView =
      activeTaskView !== "mine" || isMine;

    return matchesView && matchesSearch(task, keyword);
  });

  const kanban = document.getElementById("kanban");

  kanban.innerHTML = COLUMNS.map(column => {
    const columnTasks = filteredTasks.filter(
      task => task.status === column.status
    );

    const cards = columnTasks.map(task => `
      <div class="task"
           role="button"
           tabindex="0"
           onclick="openTask(${task.id})"
           onkeydown="if(event.key === 'Enter') openTask(${task.id})">

        <h5>${escapeHtml(task.name)}</h5>

        <p>
          ${escapeHtml(task.description || "업무 설명이 없습니다.")}
        </p>

        <div class="task-foot">
          <span class="who-mini">
            ${escapeHtml(getAssigneeLabel(task))}
          </span>

          <span class="due${isDueSoon(task.dueDate) ? " soon" : ""}">
            ~${escapeHtml(formatDueDate(task.dueDate))}
          </span>
        </div>
      </div>
    `).join("");

    return `
      <div class="col">
        <div class="col-h" style="--cc:${column.color}">
          <div class="t">
            <span class="chip ${column.chip}">
              ${column.label}
            </span>
          </div>

          <div class="cnt">${columnTasks.length}</div>
        </div>

        <div class="col-body">
          ${cards}

          ${columnTasks.length === 0
            ? `<p class="col-empty">${
                keyword ? "검색 결과 없음" : "없음"
              }</p>`
            : ""}
        </div>
      </div>
    `;
  }).join("");
}

function renderTeamPanel() {
  const teamStats = document.getElementById("teamStats");
  const teamMemberList = document.getElementById("teamMemberList");

  if (!teamStats || !teamMemberList) {
    return;
  }

  const countByStatus = status =>
    TASKS.filter(task => task.status === status).length;

  teamStats.innerHTML = `
    <div class="mp-stat">
      <div class="n mono" style="color:var(--accent)">
        ${countByStatus("ONGOING")}
      </div>
      <div class="k">진행중</div>
    </div>
    <div class="mp-stat">
      <div class="n mono" style="color:var(--wait)">
        ${countByStatus("REVIEW")}
      </div>
      <div class="k">검수 대기</div>
    </div>
    <div class="mp-stat">
      <div class="n mono" style="color:var(--reject)">
        ${countByStatus("REJECTED")}
      </div>
      <div class="k">반려</div>
    </div>
    <div class="mp-stat">
      <div class="n mono" style="color:var(--ok)">
        ${countByStatus("APPROVED")}
      </div>
      <div class="k">승인 완료</div>
    </div>
  `;

  if (TEAM_MEMBERS.length === 0) {
    teamMemberList.innerHTML = `
      <p class="team-member-empty">참여 중인 팀원이 없습니다.</p>
    `;
    return;
  }

  teamMemberList.innerHTML = TEAM_MEMBERS.map(member => {
    const memberTasks = TASKS.filter(
      task => Number(task.assigneeId) === member.id
    );
    const approvedCount = memberTasks.filter(
      task => task.status === "APPROVED"
    ).length;

    return `
      <div class="member-row clickable"
           role="button"
           tabindex="0"
           onclick="openMember(${member.id})"
           onkeydown="if(event.key === 'Enter') openMember(${member.id})">
        <span class="pic"
              style="background:${getMemberColor(member.id)}">
          ${escapeHtml(getMemberInitial(member.name))}
        </span>
        <div class="mr-info">
          <div class="nm">
            ${escapeHtml(member.name || `팀원 #${member.id}`)}
            <span class="role">
              · ${escapeHtml(getMemberRoleLabel(member))}
            </span>
          </div>
        </div>
        <div class="mr-stat">
          완료 <b>${approvedCount}</b> / 배정 ${memberTasks.length}
        </div>
        <svg class="chev" width="18" height="18"
             viewBox="0 0 24 24" fill="none"
             stroke="currentColor" stroke-width="2.4"
             stroke-linecap="round" aria-hidden="true">
          <path d="M9 18l6-6-6-6"/>
        </svg>
      </div>
    `;
  }).join("");
}

function switchTaskView(view) {
  if (!["all", "mine", "team"].includes(view)) {
    return;
  }

  activeTaskView = view;

  const isTeamView = view === "team";
  const boardPanel = document.getElementById("boardPanel");
  const teamPanel = document.getElementById("teamPanel");
  const boardTools = document.getElementById("boardTools");
  const searchInput = document.getElementById("taskSearch");

  document
    .querySelectorAll("#taskToggle button[data-p]")
    .forEach(button => {
      button.classList.toggle("on", button.dataset.p === view);
    });

  if (boardPanel) {
    boardPanel.style.display = isTeamView ? "none" : "block";
  }

  if (teamPanel) {
    teamPanel.style.display = isTeamView ? "block" : "none";
  }

  if (boardTools) {
    boardTools.style.display = isTeamView ? "none" : "flex";
  }

  if (searchInput && !isTeamView) {
    searchInput.placeholder = view === "mine"
      ? "내 업무 검색 (업무명·설명)"
      : "업무 검색 (업무명·설명)";
  }

  if (!isTeamView) {
    renderKanban();
  } else {
    renderTeamPanel();
  }
}

function openMember(memberId) {
  const member = TEAM_MEMBERS.find(item => item.id === memberId);

  if (!member) {
    return;
  }

  const memberTasks = TASKS.filter(
    task => Number(task.assigneeId) === member.id
  );
  const countByStatus = status =>
    memberTasks.filter(task => task.status === status).length;

  const memberModal = document.getElementById("memberModal");
  const memberPicture = document.getElementById("mmPic");

  document.getElementById("mmName").textContent =
    member.name || `팀원 #${member.id}`;
  document.getElementById("mmRole").textContent =
    getMemberRoleLabel(member);

  memberPicture.textContent = getMemberInitial(member.name);
  memberPicture.style.background = getMemberColor(member.id);

  document.getElementById("mmStats").innerHTML = `
    <div class="ms">
      <div class="n" style="color:var(--accent)">
        ${countByStatus("ONGOING")}
      </div>
      <div class="k">진행중</div>
    </div>
    <div class="ms">
      <div class="n" style="color:var(--wait)">
        ${countByStatus("REVIEW")}
      </div>
      <div class="k">검수 대기</div>
    </div>
    <div class="ms">
      <div class="n" style="color:var(--reject)">
        ${countByStatus("REJECTED")}
      </div>
      <div class="k">반려</div>
    </div>
    <div class="ms">
      <div class="n" style="color:var(--ok)">
        ${countByStatus("APPROVED")}
      </div>
      <div class="k">승인 완료</div>
    </div>
  `;

  const memberTaskList = document.getElementById("mmTasks");

  memberTaskList.innerHTML = memberTasks.length === 0
    ? `<p class="team-member-empty">배정된 업무가 없습니다.</p>`
    : memberTasks.map(task => {
      const column = COLUMNS.find(item => item.status === task.status);

      return `
        <div class="task-line clickable"
             style="--cc:${column ? column.color : "var(--line)"}"
             role="button"
             tabindex="0"
             onclick="openTaskFromMember(${task.id})"
             onkeydown="if(event.key === 'Enter') openTaskFromMember(${task.id})">
          <div class="tl-main">
            <h4>${escapeHtml(task.name)}</h4>
            <div class="tl-desc">
              ${escapeHtml(task.description || "업무 설명이 없습니다.")}
            </div>
          </div>
          <span class="chip ${STATUS_CHIP[task.status] || ""}">
            ${escapeHtml(STATUS_LABEL[task.status] || task.status)}
          </span>
          <span class="due">
            ~${escapeHtml(formatDueDate(task.dueDate))}
          </span>
        </div>
      `;
    }).join("");

  memberModal.classList.add("on");
  document.body.style.overflow = "hidden";
}

function closeMember() {
  const memberModal = document.getElementById("memberModal");

  if (memberModal) {
    memberModal.classList.remove("on");
  }

  document.body.style.overflow = "";
}

function openTaskFromMember(taskId) {
  closeMember();
  openTask(taskId);
}

function openTask(taskId) {
  const task = TASKS.find(item => item.id === taskId);

  if (!task) {
    return;
  }

  document.getElementById("taskModalTitle").textContent =
    task.name;

  let submission = "";

  if (task.submitTitle || task.submitContent || task.submitFile) {
    submission = `
      <div class="submission">
        <div class="slabel">제출물</div>

        <h4>
          ${escapeHtml(task.submitTitle || "제출 제목 없음")}
        </h4>

        <p>
          ${escapeHtml(task.submitContent || "제출 설명 없음")}
        </p>

        ${task.submitFile
          ? `<a class="file"
                href="${window.APP_CONTEXT}/workspace/${window.PROJECT_ID}/tasks/${task.id}/file">
               ${escapeHtml(getOriginalFileName(task.submitFile))}
             </a>`
          : ""}
      </div>
    `;
  }

  const rejected = task.status === "REJECTED"
    ? `
      <div class="reason-shown">
        <b>반려 사유</b>
        · ${escapeHtml(
          task.rejectReason || "등록된 반려 사유가 없습니다."
        )}
      </div>
    `
    : "";

  const csrfInput = `
    <input type="hidden"
           name="${escapeHtml(window.CSRF_PARAMETER)}"
           value="${escapeHtml(window.CSRF_TOKEN)}">
  `;

  const reviewActions =
    IS_LEADER && task.status === "REVIEW"
      ? `
        <div class="tm-actions task-review-actions">
          <form method="post"
                action="${window.APP_CONTEXT}/workspace/${window.PROJECT_ID}/tasks/${task.id}/approve"
                onsubmit="return confirm('이 업무를 승인할까요?')">
            ${csrfInput}
            <button type="submit" class="btn pri">승인</button>
          </form>
          <button type="button"
                  class="btn ghost task-reject-button"
                  onclick="openReject(${task.id})">
            반려
          </button>
        </div>
      `
      : "";

  const rejectionAcknowledge =
    task.status === "REJECTED"
    && Number(task.assigneeId) === Number(window.LOGIN_MEMBER_ID)
      ? `
        <form class="tm-actions"
              method="post"
              action="${window.APP_CONTEXT}/workspace/${window.PROJECT_ID}/tasks/${task.id}/acknowledge-rejection"
              onsubmit="return confirm('반려 사유를 확인했나요? 확인하면 업무가 진행중으로 이동합니다.')">
          ${csrfInput}
          <button type="submit" class="btn pri">
            반려 사유 확인
          </button>
        </form>
      `
      : "";

  const canSubmit =
    Number(task.assigneeId) === Number(window.LOGIN_MEMBER_ID)
    && task.status === "ONGOING";

  const submitButton = canSubmit
    ? `
      <div class="tm-actions">
        <button type="button"
                class="btn pri"
                onclick="openSubmit(${task.id})">
          결과물 제출
        </button>
      </div>
    `
    : "";

  const deleteAction = IS_LEADER
    ? `
      <form class="tm-actions task-delete-action"
            method="post"
            action="${window.APP_CONTEXT}/workspace/${window.PROJECT_ID}/tasks/${task.id}/delete"
            onsubmit="return confirm('이 업무를 삭제할까요? 삭제한 업무는 복구할 수 없습니다.')">
        ${csrfInput}
        <button type="submit" class="btn ghost task-delete-button">
          업무 삭제
        </button>
      </form>
    `
    : "";

  document.getElementById("taskModalBody").innerHTML = `
    <div class="tm-meta">
      <span class="chip ${STATUS_CHIP[task.status] || ""}">
        ${escapeHtml(STATUS_LABEL[task.status] || task.status)}
      </span>

      <span class="who-mini">
        ${escapeHtml(getAssigneeLabel(task))}
      </span>

      <span class="tm-sub mono">
        마감 ~${escapeHtml(formatDueDate(task.dueDate))}
      </span>
    </div>

    <p class="tm-desc">
      ${escapeHtml(task.description || "업무 설명이 없습니다.")}
    </p>

    ${submission}
    ${rejected}
    ${reviewActions}
    ${rejectionAcknowledge}
    ${submitButton}
    ${deleteAction}
  `;

  document.getElementById("taskModal").classList.add("on");
  document.body.style.overflow = "hidden";
}

function closeTask() {
  document.getElementById("taskModal").classList.remove("on");
  document.body.style.overflow = "";
}

function openSubmit(taskId) {
  const task = TASKS.find(item => item.id === taskId);

  if (!task) {
    return;
  }

  const isAssignee =
    Number(task.assigneeId) === Number(window.LOGIN_MEMBER_ID);

  const allowedStatus =
    task.status === "ONGOING";

  if (!isAssignee || !allowedStatus) {
    alert("본인에게 배정된 진행 중 업무만 제출할 수 있습니다.");
    return;
  }

  const form = document.getElementById("submitForm");
  const modal = document.getElementById("submitModal");

  if (!form || !modal) {
    console.error("제출 폼 또는 제출 모달을 찾을 수 없습니다.");
    return;
  }

  form.action =
    window.APP_CONTEXT
    + "/workspace/"
    + window.PROJECT_ID
    + "/tasks/"
    + task.id
    + "/submit";

  form.reset();

  const fileName = document.getElementById("submitFileName");

  if (fileName) {
    fileName.textContent = "선택된 파일 없음";
  }

  document.getElementById("smTaskName").textContent =
    task.name || "업무명 없음";

  document.getElementById("smTaskDue").textContent =
    "마감 " + formatDueDate(task.dueDate);

  closeTask();

  modal.classList.add("on");
  document.body.style.overflow = "hidden";
}

function closeSubmit() {
  const modal = document.getElementById("submitModal");

  if (modal) {
    modal.classList.remove("on");
  }

  document.body.style.overflow = "";
}

function openReject(taskId) {
  const task = TASKS.find(item => item.id === taskId);

  if (!task || !IS_LEADER || task.status !== "REVIEW") {
    alert("팀장만 검수 대기 중인 업무를 반려할 수 있습니다.");
    return;
  }

  const form = document.getElementById("rejectForm");
  const modal = document.getElementById("rejectModal");

  if (!form || !modal) {
    return;
  }

  form.action =
    window.APP_CONTEXT
    + "/workspace/"
    + window.PROJECT_ID
    + "/tasks/"
    + task.id
    + "/reject";

  form.reset();
  document.getElementById("rejectTaskName").textContent = task.name;

  closeTask();
  modal.classList.add("on");
  document.body.style.overflow = "hidden";

  document.getElementById("rejectReason").focus();
}

function closeReject() {
  const modal = document.getElementById("rejectModal");

  if (modal) {
    modal.classList.remove("on");
  }

  document.body.style.overflow = "";
}

window.onTaskSearch = value => {
  taskSearch = value;
  renderKanban();
};

const taskToggle = document.getElementById("taskToggle");

if (taskToggle) {
  taskToggle.addEventListener("click", event => {
    const button = event.target.closest("button[data-p]");

    if (button) {
      switchTaskView(button.dataset.p);
    }
  });
}

window.openTask = openTask;
window.closeTask = closeTask;
window.openSubmit = openSubmit;
window.closeSubmit = closeSubmit;
window.openReject = openReject;
window.closeReject = closeReject;
window.openMember = openMember;
window.closeMember = closeMember;
window.openTaskFromMember = openTaskFromMember;

const submitFileInput = document.getElementById("submitFile");
const submitFileName = document.getElementById("submitFileName");

if (submitFileInput && submitFileName) {
  submitFileInput.addEventListener("change", () => {
    const file = submitFileInput.files[0];

    submitFileName.textContent = file
      ? file.name
      : "선택된 파일 없음";
  });
}

document.addEventListener("keydown", event => {
  if (event.key === "Escape") {
    closeTask();
    closeSubmit();
    closeReject();
    closeMember();
  }
});

switchTaskView("all");
