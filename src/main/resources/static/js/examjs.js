var jsonData, currentIndex = 0, qno = 1, seconds = 30, a, b, c, d, item, score = 0, total = 0;
var shuffledOptions = [], timer;

// Function to shuffle options
function shuffleOptions() {
	shuffledOptions = ['A', 'B', 'C', 'D'].sort(() => Math.random() - 0.5);
}

// Function to check the selected answer
function checkAnswer() {
	if (a.checked && shuffledOptions[0] === item.correct) score++;
	else if (b.checked && shuffledOptions[1] === item.correct) score++;
	else if (c.checked && shuffledOptions[2] === item.correct) score++;
	else if (d.checked && shuffledOptions[3] === item.correct) score++;
}

// Function to display the current record
function displayCurrentRecord() {
	var lblqno = document.getElementById("lblqno");
	var lblquestion = document.getElementById("lblquestion");
	a = document.getElementById("a");
	b = document.getElementById("b");
	c = document.getElementById("c");
	d = document.getElementById("d");
	var lblA = document.getElementById("lblA");
	var lblB = document.getElementById("lblB");
	var lblC = document.getElementById("lblC");
	var lblD = document.getElementById("lblD");

	a.checked = b.checked = c.checked = d.checked = false; // Reset radio buttons

	if (jsonData && jsonData.length > 0) {
		item = jsonData[currentIndex];
		lblqno.innerHTML = qno;
		lblquestion.innerHTML = item.question;

		// Shuffle and display options
		shuffleOptions();
		lblA.innerHTML = item[shuffledOptions[0].toLowerCase()];
		lblB.innerHTML = item[shuffledOptions[1].toLowerCase()];
		lblC.innerHTML = item[shuffledOptions[2].toLowerCase()];
		lblD.innerHTML = item[shuffledOptions[3].toLowerCase()];
	}
}

// Function to start the timer
function startTimer() {
	var timerDisplay = document.getElementById('timer');
	seconds = 30; // Reset the timer to 30 seconds
	timerDisplay.textContent = seconds;

	clearInterval(timer); // Ensure no overlapping intervals
	timer = setInterval(function() {
		seconds--;
		timerDisplay.textContent = seconds;

		if (seconds <= 0) {
			clearInterval(timer);
			nextQuestion(); // Move to the next question when time runs out
		}
	}, 1000);
}

// Function to start the test
function startTest() {
	jsonData = JSON.parse($("#jsonData").val());
	total = jsonData.length; // Ensure total is set at the start
	displayCurrentRecord();
	startTimer();
	disableKeyboard();
}

// Function to move to the next question
function nextQuestion() {
	checkAnswer();
	currentIndex++;
	qno++;
	if (currentIndex === jsonData.length) {
		// Ensure total and score are updated before form submission
		document.getElementById('totalInput').value = total;
		document.getElementById('scoreInput').value = score;
		document.getElementById('hiddenForm').submit();
	} else {
		displayCurrentRecord();
		startTimer();
	}
}


/////////////////////////////////////
////////////////////////////////////
// Variables to track full-screen exits
let fullScreenExitCount = 0;

function gofullscreen() {
	const element = document.documentElement;
	if (element.requestFullscreen) {
		element.requestFullscreen();
	} else if (element.webkitRequestFullscreen) {
		element.webkitRequestFullscreen();
	} else if (element.msRequestFullscreen) {
		element.msRequestFullscreen();
	} else {
		alert('Fullscreen mode is not supported by your browser.');
	}

	// Listen for exit from full screen
	document.addEventListener('fullscreenchange', checkFullScreenExit);
	document.addEventListener('webkitfullscreenchange', checkFullScreenExit);
	document.addEventListener('msfullscreenchange', checkFullScreenExit);
}

// Check if the user exits full screen
function checkFullScreenExit() {
	if (!document.fullscreenElement && !document.webkitIsFullScreen && !document.msFullscreenElement) {
		fullScreenExitCount++;
		if (fullScreenExitCount >= 2) {
			// Before submitting, ensure total and score are correct
			document.getElementById('totalInput').value = total;
			document.getElementById('scoreInput').value = score;
			document.getElementById('hiddenForm').submit(); // Automatically submit the test
			alert("You exited full screen twice. The test will now be submitted.");
		} else {
			alert(`You exited full screen. Please return to full screen to continue the test. Other Wise your test will be automatically will be submitted.`);
			gofullscreen(); // Prompt the user to return to full screen
		}
	}
}

// Delay timer until modal is closed
document.addEventListener('DOMContentLoaded', () => {
	const modal = document.getElementById('modal');
	modal.style.display = 'flex';

	document.getElementById('closeModal').addEventListener('click', () => {
		modal.style.display = 'none';
		gofullscreen();
		startTest(); // Start the test when the modal is closed
	});
});




// Function to disable all keyboard keys during the exam
function disableKeyboard() {
	document.addEventListener('keydown', function(e) {
		e.preventDefault(); // Prevent the default action of the key press
	});
}


document.addEventListener('keydown', function(e) {
	// Disable Ctrl+T (new tab)
	if ((e.ctrlKey || e.metaKey) && e.key === 't') {
		e.preventDefault();
	}
	// Disable Ctrl+N (new window)
	if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
		e.preventDefault();
	}
	// Disable Ctrl+W (close tab)
	if ((e.ctrlKey || e.metaKey) && e.key === 'w') {
		e.preventDefault();
	}
	// Disable Escape key
	if (e.key === 'Escape') {
		e.preventDefault();
	}

	// Disable Alt key
	if (e.key === 'Alt') {
		e.preventDefault();
	}
});

// Disable right-click context menu
document.addEventListener('contextmenu', function(e) {
	e.preventDefault();
});

// Prevent window close (browser-level)
window.addEventListener('beforeunload', function(e) {
	//e.preventDefault();
	e.returnValue = '';// Chrome requires returnValue to be set
});


$(document).ready(function() {
	$('#ButtonNext').on('click', gofullscreen);
});

// Event listener for "Next" button
$(document).ready(function() {
	$('#ButtonNext').on('click', nextQuestion);
});


