document.addEventListener("DOMContentLoaded", function () {
	var univSelect = document.querySelector('select[name="univ_name"]');
	var deptSelect = document.querySelector('select[name="dept_id"]');
	var univPlaceholderText = "학교를 선택하세요";
	var deptPlaceholderText = "학과를 선택하세요";

	if (!univSelect || !deptSelect) {
		return;
	}

	var initialUniv = univSelect.value;
	var initialDept = deptSelect.value;

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
			var name = option.value || option.textContent.trim();
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

	function renderDeptOptions(univName, preferredDeptValue) {
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
	}

	renderDeptOptions(univSelect.value, initialDept);

	univSelect.addEventListener("change", function () {
		renderDeptOptions(univSelect.value, "");
	});
});
