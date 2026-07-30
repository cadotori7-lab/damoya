document.addEventListener("DOMContentLoaded", function () {
	var univSelect = document.querySelector('select[name="univ_name"]');
	var deptSelect = document.querySelector('select[name="dept_id"]');
	var doubleMajorSelect = document.querySelector('select[name="double_major"]');
	var univPlaceholderText = "학교를 선택하세요";
	var deptPlaceholderText = "학과를 선택하세요";

	if (!univSelect || !deptSelect) {
		return;
	}

	var initialUniv = univSelect.value;
	var initialDept = deptSelect.value;
	var initialDoubleMajor = doubleMajorSelect ? doubleMajorSelect.value : "";

	var deptSource = Array.from(deptSelect.options).filter(function (option) {
		return option.value !== "";
	}).map(function (option) {
		return {
			value: option.value,
			label: option.textContent.trim(),
			univName: option.dataset.univName || "",
		};
	});

	var uniqueUnivNames = [];
	var seenUniv = new Set();

	deptSource.forEach(function (dept) {
		if (dept.univName && !seenUniv.has(dept.univName)) {
			seenUniv.add(dept.univName);
			uniqueUnivNames.push(dept.univName);
		}
	});

	if (uniqueUnivNames.length === 0) {
		Array.from(univSelect.options).forEach(function (option) {
			// value가 비어 있으면 플레이스홀더이므로 학교 목록에 넣지 않는다
			var name = (option.value || "").trim();
			if (name && !seenUniv.has(name)) {
				seenUniv.add(name);
				uniqueUnivNames.push(name);
			}
		});
	}

	univSelect.innerHTML = "";
	var univPlaceholder = document.createElement("option");
	univPlaceholder.value = "";
	univPlaceholder.textContent = univPlaceholderText;
	univPlaceholder.disabled = true;
	univPlaceholder.selected = true;
	univSelect.appendChild(univPlaceholder);

	uniqueUnivNames.forEach(function (name) {
		var option = document.createElement("option");
		option.value = name;
		option.textContent = name;
		univSelect.appendChild(option);
	});

	var selectedUniv = uniqueUnivNames.includes(initialUniv) ? initialUniv : "";
	if (selectedUniv) {
		univSelect.value = selectedUniv;
	}

	function renderDoubleMajorOptions(univName, preferredDoubleMajor) {
		if (!doubleMajorSelect) {
			return;
		}
		doubleMajorSelect.innerHTML = "";

		var placeholder = document.createElement("option");
		placeholder.value = "";
		placeholder.textContent = "없음";
		doubleMajorSelect.appendChild(placeholder);

		if (!univName) {
			return;
		}

		var matched = deptSource.filter(function (dept) {
			return dept.univName === univName && dept.value !== deptSelect.value;
		});

		matched.forEach(function (dept) {
			var option = document.createElement("option");
			if (dept.label === preferredDoubleMajor) {
				option.selected = true;
			}
			option.value = dept.label; // 복수전공은 학과명 문자열 그대로 저장
			option.textContent = dept.label;
			doubleMajorSelect.appendChild(option);
		});

		var isPreferredValid = matched.some(function (dept) {
			return dept.label === preferredDoubleMajor;
		});

		if (isPreferredValid) {
			doubleMajorSelect.value = preferredDoubleMajor;
		}
	}

	function renderDeptOptions(univName, preferredDeptValue, preferredDoubleMajor) {
		deptSelect.innerHTML = "";

		var deptPlaceholder = document.createElement("option");
		deptPlaceholder.value = "";
		deptPlaceholder.textContent = deptPlaceholderText;
		deptPlaceholder.disabled = true;
		deptPlaceholder.selected = true;
		deptSelect.appendChild(deptPlaceholder);

		if (!univName) {
			deptSelect.disabled = true;
			return;
		}

		var matched = deptSource.filter(function (dept) {
			return dept.univName === univName;
		});

		deptSelect.disabled = false;

		if (matched.length === 0) {
			deptPlaceholder.textContent = "학과 정보가 없습니다.";
			return;
		}

		matched.forEach(function (dept) {
			var option = document.createElement("option");
			option.value = dept.value;
			option.textContent = dept.label;
			deptSelect.appendChild(option);
		});

		var isPreferredValid = matched.some(function (dept) {
			return dept.value === preferredDeptValue;
		});

		if (isPreferredValid) {
			deptSelect.value = preferredDeptValue;
		}

		renderDoubleMajorOptions(univName, preferredDoubleMajor);
	}

	renderDeptOptions(univSelect.value, initialDept, initialDoubleMajor);

	univSelect.addEventListener("change", function () {
		renderDeptOptions(univSelect.value, "", "");
	});

	// 주전공(학과)을 바꾸면, 방금 고른 학과는 복수전공 목록에서 빠지도록 다시 그림
	deptSelect.addEventListener("change", function () {
		var currentDoubleMajor = doubleMajorSelect ? doubleMajorSelect.value : "";
		renderDoubleMajorOptions(univSelect.value, currentDoubleMajor);
	});
});
