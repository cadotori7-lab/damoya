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

let taskSearch = "";

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
  const filteredTasks = TASKS.filter(task =>
    matchesSearch(task, keyword)
  );

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
            담당자 #${escapeHtml(task.assigneeId || "미정")}
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

        ${cards}

        ${columnTasks.length === 0
          ? `<p class="col-empty">${
              keyword ? "검색 결과 없음" : "없음"
            }</p>`
          : ""}
      </div>
    `;
  }).join("");
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
          ? `<div class="file">
               ${escapeHtml(task.submitFile)}
             </div>`
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

  document.getElementById("taskModalBody").innerHTML = `
    <div class="tm-meta">
      <span class="chip ${STATUS_CHIP[task.status] || ""}">
        ${escapeHtml(STATUS_LABEL[task.status] || task.status)}
      </span>

      <span class="who-mini">
        담당자 #${escapeHtml(task.assigneeId || "미정")}
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
  `;

  document.getElementById("taskModal").classList.add("on");
  document.body.style.overflow = "hidden";
}

function closeTask() {
  document.getElementById("taskModal").classList.remove("on");
  document.body.style.overflow = "";
}

window.onTaskSearch = value => {
  taskSearch = value;
  renderKanban();
};

window.openTask = openTask;
window.closeTask = closeTask;

document.addEventListener("keydown", event => {
  if (event.key === "Escape") {
    closeTask();
  }
});

renderKanban();