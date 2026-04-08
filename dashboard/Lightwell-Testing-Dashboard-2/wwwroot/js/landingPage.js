$(document).ready(function () { setup(); });

let resultsTable = document.getElementById("resultsTableBody");
let totalPassedCell = document.getElementById("totalPassedCell");
let totalFailedCell = document.getElementById("totalFailedCell");
let totalTestsCell = document.getElementById("totalTestsCell");
let totalPassedPercentageCell = document.getElementById("totalPassedPercentageCell");
let totalFailedPercentageCell = document.getElementById("totalFailedPercentageCell");
let totalDurationCell = document.getElementById("totalDurationCell");
let totalTestDurationCell = document.getElementById("totalTestDurationCell");
let longestBuildDurationDiv = document.getElementById("longestBuildDurationDiv");
let shortestBuildDurationDiv = document.getElementById("shortestBuildDurationDiv");
let longestTestDurationDiv = document.getElementById("longestTestDurationDiv");
let shortestTestDurationDiv = document.getElementById("shortestTestDurationDiv");
let mostFailsDiv = document.getElementById("mostFailsDiv");
let mostScenariosDiv = document.getElementById("mostScenariosDiv");
let landingSpace = document.getElementById("landingSpace");

let totalPassed = 0;
let totalFailed = 0;
let totalTests = 0;
let totalBuildMinutes = 0;
let totalTestMinutes = 0;

let mostFailingScenariosTestResults = [];
let mostScenariosTestResults = [];
let longestBuildDurationTestResults = [];
let shortestBuildDurationTestResults = [];
let longestTestDurationTestResults = [];
let shortestTestDurationTestResults = [];
let failedBuildPaths = [];

function setup() {
    activateSpinner();
    landingSpace.style.display = "none";
    commonSetup(landingRefresh);
    getTestResults(createPage);
    google.charts.load('current', { 'packages': ['corechart'] });

    $('#controlDiv').removeAttr('hidden');
    //$('#buildAll').hide();
    //$('#buildFailing').hide();
    $('#filterControlContainer').hide();
    $('#upALevel').hide();
    $('#folderProgress').hide();

    $('#refreshButton').click(function () {
        landingRefresh();
    });

    $('#buildAll').click(function () {
        buildAllButtonClick();
    });

    $('#buildFailing').click(function () {
        buildAllFailingButtonClick();
    });

    setTotals();

    hideSpinner();
}

function landingRefresh() {
    activateSpinner();

    totalPassed = 0;
    totalFailed = 0;
    totalTests = 0;
    totalTestMinutes = 0;
    totalBuildMinutes = 0;

    mostFailingScenariosTestResults = [];
    mostScenariosTestResults = [];
    longestBuildDurationTestResults = [];
    shortestBuildDurationTestResults = [];
    longestTestDurationTestResults = [];
    shortestTestDurationTestResults = [];

    landingSpace.style.display = "none";
    clear();
    getTestResults(createPage, true);
    google.charts.load('current', { 'packages': ['corechart'] });
    setTotals();

    lastRefreshUpdate();

    hideSpinner();
}

function clear() {
    $("#resultsTableBody tr").remove();
    $("#longestBuildDurationDiv p").remove();
    $("#shortestBuildDurationDiv p").remove();
    $("#longestTestDurationDiv p").remove();
    $("#shortestTestDurationDiv p").remove();
    $("#mostFailsDiv p").remove();
    $("#mostScenariosDiv p").remove();
}

function createPage() {

    failedBuildPaths = [];

    testResults.buildResults.forEach(function (testResult) {
        if (testResult.result !== "disabled") { 
            row = createTestResultRow(testResult);
            resultsTable.appendChild(row);
        }
    });

    $('.editable').on('click', function (event) {
        if (event.target.tagName !== 'A') { 
            doEdit($(this));
        }
    });

    totalPassedCell.innerText = totalPassed;
    totalFailedCell.innerText = totalFailed;
    totalTestsCell.innerText = totalTests;

    totalPassedPercentageCell.innerText = ((totalPassed / totalTests) * 100).toFixed(2) + "%";
    totalFailedPercentageCell.innerText = ((totalFailed / totalTests) * 100).toFixed(2) + "%";
    totalDurationCell.innerText = getDurationString(totalBuildMinutes);
    totalTestDurationCell.innerText = getDurationString(totalTestMinutes);
    google.charts.setOnLoadCallback(drawChart);

    if (SHOW_MILESTONES_ON_LANDING) {
        $('#jiraFrame').hide();
        $('#milestone').show();
    }
    populateMostWidgets();
    landingSpace.style.display = "flex";
    setTotals();
}

function createTestResultRow(testResult) {

    let row = document.createElement("tr");

    testResult.link = createLatestLink(testResult.parent, testResult.buildName, testResult.buildNumber, testResult.buildName);

    let name = td(testResult.link, "html");
    let passed = td(testResult.passedTests, "text", "centerText");
    if (testResult.passedTests > 0) {
        totalPassed += testResult.passedTests;
    }

    let failed = td(testResult.failedTests, "text", "centerText");
    mostFailingScenariosTestResults = checkForMostResult(testResult, mostFailingScenariosTestResults, "failedTests");
    if (testResult.failedTests > 0) {
        totalFailed += testResult.failedTests;
    }

    let total = td(testResult.totalTests, "text", "centerText");
    mostScenariosTestResults = checkForMostResult(testResult, mostScenariosTestResults, "totalTests");
    if (testResult.totalTests > 0) {
        totalTests += testResult.totalTests;
    }

    totalTestMinutes += getDurationInMinutes(testResult.junitDurationString);
    checkForMostTestDurationResult(testResult);
    let testDuration = td(testResult.junitDurationString, "text", "centerText");

    totalBuildMinutes += getDurationInMinutes(testResult.duration);
    checkForMostBuildDurationResult(testResult);
    let buildDuration = td(testResult.duration, "text", "centerText");

    let resultClass = getResultClass(testResult.result);
    if (testResult.result != "SUCCESS" && testResult.result != "Building" && testResult.result != "disabled") {
        failedBuildPaths.push(createBuildPath(testResult.parent, testResult.buildName));
    }

    let result = td(toTitleCase(testResult.result), "text", "centerText " + resultClass);

    let displayedDescription = embedJiraLinks(testResult.description);
    let description = td(span(displayedDescription), "html", "centerText limitedWidth editable");
    description.setAttribute("buildpath", createLink(testResult.parent, testResult.buildName, "", BUILD_DEFINITION).href);
    description.setAttribute("buildname", convertToNaturalPhrase(testResult.buildName));
    description.setAttribute("originaltext", testResult.description);

    let button = td(createIconBuildButton(testResult.parent, testResult.buildName, testResult.result, testResult.buildNumber), "html");

    row.appendChild(name);
    row.appendChild(passed);
    row.appendChild(failed);
    row.appendChild(total);
    row.appendChild(testDuration);
    row.appendChild(buildDuration);
    row.appendChild(result);
    row.appendChild(description);
    row.appendChild(button);

    return row;
}

function doEdit(cell) {
    currentCell = cell; // Save reference to the clicked cell
    //const cellText = currentCell.text(); // Get the cell's text
    $('#editModalLabel').text(currentCell.attr('buildname'));
    $('#editBox').val(cell.attr('originaltext')); // Set the text in the modal's textarea
    $('#editModal').fadeIn(); // Show the modal
}
function embedJiraLinks(description) {
    const jiraRegex = /\b[A-Z]+-\d+\b/gi;

    // Replace matches with links
    const jiraBaseUrl = "<<ATLASSIAN LINK>>";
    const result = description.replace(jiraRegex, (match) => {
        return `<a href="${jiraBaseUrl}${match}" target="_blank">${match}</a>`;
    });

    return result;
}

function toTitleCase(text) {
    if (text === undefined || text === null) {
        text = "";
    }
    return text.charAt(0).toUpperCase() + text.substr(1).toLowerCase();
}

function checkForMostResult(testResult, mostArray, propertyName) {
    if (SHOW_MILESTONES_ON_LANDING) {
        if (mostArray.length == 0) {
            mostArray.push(testResult);
            return mostArray;
        }

        if (testResult[propertyName] > mostArray[0][propertyName]) {
            mostArray = [];
            mostArray.push(testResult);
        } else if (testResult[propertyName] === mostArray[0][propertyName]) {
            mostArray.push(testResult);
        }
    }

    return mostArray;
}

function checkForMostBuildDurationResult(testResult) {
    if (SHOW_MILESTONES_ON_LANDING) {
        let durationInMinutes = getDurationInMinutes(testResult.duration);

        if (durationInMinutes > 0) {
            if (shortestBuildDurationTestResults.length == 0) {
                shortestBuildDurationTestResults.push(testResult);
            } else if (durationInMinutes < getDurationInMinutes(shortestBuildDurationTestResults[0].duration)) {
                shortestBuildDurationTestResults = [];
                shortestBuildDurationTestResults.push(testResult);
            } else if (durationInMinutes === getDurationInMinutes(shortestBuildDurationTestResults[0].duration)) {
                shortestBuildDurationTestResults.push(testResult);
            }

            if (longestBuildDurationTestResults.length == 0) {
                longestBuildDurationTestResults.push(testResult);
            } else if (durationInMinutes > getDurationInMinutes(longestBuildDurationTestResults[0].duration)) {
                longestBuildDurationTestResults = [];
                longestBuildDurationTestResults.push(testResult);
            } else if (durationInMinutes === getDurationInMinutes(longestBuildDurationTestResults[0].duration)) {
                longestBuildDurationTestResults.push(testResult);
            }
        }
    }
}

function checkForMostTestDurationResult(testResult) {
    if (SHOW_MILESTONES_ON_LANDING) {
        let durationInMinutes = getDurationInMinutes(testResult.junitDurationString);

        if (durationInMinutes > 0) {
            if (shortestTestDurationTestResults.length == 0) {
                shortestTestDurationTestResults.push(testResult);
            } else if (durationInMinutes < getDurationInMinutes(shortestTestDurationTestResults[0].junitDurationString)) {
                shortestTestDurationTestResults = [];
                shortestTestDurationTestResults.push(testResult);
            } else if (durationInMinutes === getDurationInMinutes(shortestTestDurationTestResults[0].junitDurationString)) {
                shortestTestDurationTestResults.push(testResult);
            }

            if (longestTestDurationTestResults.length == 0) {
                longestTestDurationTestResults.push(testResult);
            } else if (durationInMinutes > getDurationInMinutes(longestTestDurationTestResults[0].junitDurationString)) {
                longestTestDurationTestResults = [];
                longestTestDurationTestResults.push(testResult);
            } else if (durationInMinutes === getDurationInMinutes(longestTestDurationTestResults[0].junitDurationString)) {
                longestTestDurationTestResults.push(testResult);
            }
        }
    }
}

function getResultClass(result) {
    let resultClass = "";
    switch (result) {
        case "FAILURE":
            resultClass = "failure";
            break;
        case "SUCCESS":
            resultClass = "pass";
            break;
        case "NOT RUN":
            resultClass = "notRun";
            break;
        case "Building":
            resultClass = "building";
            break;
        case "Queued":
            resultClass = "queued";
            break;
        case "ERROR":
            resultClass = "error";
            break;
        case "not found":
        default:
            resultClass = "dangerOrange";
            break;
    }

    return resultClass;
}

function getDurationInMinutes(durationString) {

    let minutes = 0;

    if (durationString !== null && durationString !== "-") {
        let durationParts = durationString.split(":");
        minutes += Number(durationParts[1]);

        minutes += Number(durationParts[0]) * 60;
    }

    return minutes;
}

function getDurationString(totalMinutes) {
    let hours = Math.floor(totalMinutes / 60);
    let minutes = totalMinutes % 60;
    let durationString = hours + ":";
    if (minutes < 10) {
        durationString += "0";
    }
    durationString += minutes;
    return durationString;
}

function drawChart() {

    var data = google.visualization.arrayToDataTable([
        ['Tests', 'Number'],
        ['Passing', totalPassed],
        ['Failing', totalFailed]
    ]);

    var options = {
        title: 'OVERALL TEST PROGRESS',
        colors: ['rgb(146, 221, 150)', 'rgb(242, 146, 140)'],
        backgroundColor: '#f2f2f2',
        pieSliceTextStyle: { color: 'black' }
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart'));

    chart.draw(data, options);
}

function populateMostWidgets() {

    populateWidget(longestBuildDurationDiv, longestBuildDurationTestResults, "Duration", "duration");
    populateWidget(shortestBuildDurationDiv, shortestBuildDurationTestResults, "Duration", "duration");
    populateWidget(longestTestDurationDiv, longestTestDurationTestResults, "Duration", "junitDurationString");
    populateWidget(shortestTestDurationDiv, shortestTestDurationTestResults, "Duration", "junitDurationString");
    populateWidget(mostFailsDiv, mostFailingScenariosTestResults, "Scenarios", "failedTests");
    populateWidget(mostScenariosDiv, mostScenariosTestResults, "Scenarios", "totalTests");
}

//<p><b>Feature:</b> <a href="#">Feature name</a> <b>Duration:</b> <span id="longestDurationSpan"></span></p>

function populateWidget(widget, mostArray, label, propertyName) {

    mostArray.forEach(function (testResult) {
        let rowHtml = "<b>Feature:</b> <a href='" + testResult.link.href + "' target='_blank'>" + testResult.link.innerText + "</a>";
        rowHtml += " <b>" + label + ": </b> " + testResult[propertyName] + " ";

        let row = p();
        row.innerHTML = rowHtml;

        widget.appendChild(row);
    });
}

function buildAllButtonClick() {
    var answer = confirm("Are you sure you want to build all of the builds?");
    if (answer) {
        let buildPaths = [];
        testResults.buildResults.forEach(function (build) {
            if (!['Building', 'disabled'].includes(build.result)) {
                buildPaths.push(createBuildPath(build.parent, build.buildName));
            }
        })

        triggerBuilds(buildPaths, "ALL");
    }
}

function buildAllFailingButtonClick() {
    var answer = confirm("Are you sure you want to build all of the failing builds?");
    if (answer) {
        triggerBuilds(failedBuildPaths, "Failed");
    }
}