let testResults = [];
let buildPaths = {};
let isolateExpansionActivity = false;

let CUCUMBER_REPORTS_LINK_PART = "/cucumber-html-reports";
let BUILD = "Build";
let BUILD_DEFINITION = "Build Definition";
let LATEST = "Latest";
let PREVIOUS = "Previous";
let BUILD_NUMBER = "buildNumber";
let JOB = "/job/";
let filters = [];

let LINK_ROOT = JENKINS_PROTOCOL + "://" + getHostName() + ":" + JENKINS_PORT + JENKINS_PREFIX + JOB;

let collections = [];

let refreshFunction;

function commonSetup(refreshFunctionCallback) {
    $('#logoPic').attr('src', './images/' + LOGO_PIC);
    $('#sideBar').attr('style', 'background-image: linear-gradient(to bottom,' + SIDE_BAR_TOP_COLOR + ',' + SIDE_BAR_BOTTOM_COLOR + ');');
    $('#jenkinsLink').attr('href', JENKINS_PROTOCOL + "://" + getHostName() + ":" + JENKINS_PORT + JENKINS_PREFIX);
    document.querySelector(':root').style.setProperty('--brand-color', SIDE_BAR_BOTTOM_COLOR)

    $('#dashboardLink').attr('href', getOrigin());
    $('#statusLink').attr('href', getOrigin() + "/Status");
    $('#summaryLink').attr('href', getOrigin() + "/Summary");
    $('#summaryLink').text(SUMMARY_PAGE_NAME);

    refreshFunction = refreshFunctionCallback;

    if (REFRESH_INTERVAL !== 0) {
        let timer = setInterval(refreshFunction, REFRESH_INTERVAL);
    }

    lastRefreshUpdate();
}

function CreateLinks(testResult) {
    let parent = testResult["parent"];
    let buildName = testResult["buildName"];
    let latestBuildnumber = testResult[BUILD_NUMBER];
    let previousBuildNumber = latestBuildnumber - 1;

    var linkSpan = document.createElement("span");

    //linkBuild
    let hrefAddition = "";
    let linkBuildElement = createLink(parent, buildName, hrefAddition, BUILD_DEFINITION);

    //linkLatest
    let linkLatestElement = createLatestLink(parent, buildName, testResult[BUILD_NUMBER]);

    //linkPrevious
    let linkPreviousElement = null;

    if (previousBuildNumber > 0) {
        hrefAddition = "/" + (testResult[BUILD_NUMBER] - 1) + CUCUMBER_REPORTS_LINK_PART;
        linkPreviousElement = createLink(parent, buildName, hrefAddition, PREVIOUS);   
    }

    linkSpan.appendChild(linkLatestElement);
    linkSpan.innerHTML += " | ";

    if (linkPreviousElement !== null) {
        linkSpan.appendChild(linkPreviousElement);
        linkSpan.innerHTML += " | ";
    }
    linkSpan.appendChild(linkBuildElement);

    return linkSpan;
}

function createLink(parent, buildName, hrefAddition, linkText) {
    var link = document.createElement("a");
    link.href = getLinkBase(parent, buildName) + hrefAddition;
    link.innerText = linkText;
    link.target = "_blank";

    return link;
}

function createLatestLink(parent, buildName, buildNumber, alternateLinkText) {
    hrefAddition = "/" + buildNumber + CUCUMBER_REPORTS_LINK_PART;

    let linkText = alternateLinkText !== undefined ? alternateLinkText : LATEST;
    return createLink(parent, buildName, hrefAddition, linkText);
}

function getLinkBase(parent, buildName) {
    if (parent === "main") {
        linkBase = LINK_ROOT + buildName;
    } else {
        var sanitizedParentRoot = parent.replace(" ", "%20").replace(/[.]/g, JOB);
        linkBase = LINK_ROOT + sanitizedParentRoot + JOB + buildName;
    }

    return linkBase;
}

function getHostName() {
    if (JENKINS_HOST !== undefined) {
        return JENKINS_HOST
    }
    return window.location.host.split(":")[0];
}

function getOrigin() {
    return window.location.origin;
}

function getLocation() {
    return window.location;
}

function getPathname() {
    return window.location.pathname
}

function createTestResultSection(testResult) {
    //create link
    testResult["link"] = CreateLinks(testResult);

    if (testResult["parent"] !== undefined) {
        var parentName = testResult["parent"].replace(" ", "");

        var parentId = parentName;
        var parentContainer = document.getElementById(parentId);

        var testDiv = createResultDiv(testResult, parentId);

        checkIfTabTotalValuesAreDefined(testResult["parent"]);

        var passed = testResult["passedTests"];
        var total = testResult["totalTests"];

        //TODO
        if (passed > 0) {
            totalPassed += passed;
            //totalPassedByTab[parentId] += passed;
        }
        if (total > 0) {
            totalTests += total;
            //totalTestsByTab[parentId] += total
        }

        parentContainer.appendChild(testDiv);

        updateTabSuccess(parentName);
        setTotals();
    }
}

function getParentName(parentPaths) {
    var parentName = "";

    if (parentPaths !== null && parentPaths.includes('.')) {
        var paths = parentPaths.split(".");
        for (var x = 0; x < paths.length - 1; x++) {
            if (parentName.length !== 0) {
                parentName += ".";
            }
            parentName += paths[x];
        }
    }

    if (parentName == "") {
        parentName = "testResultSpace";
    }

    return parentName;
}

function getChildName(parentPaths) {
    var paths = parentPaths.split(".");
    var childId = paths[paths.length - 1];

    return childId;
}

function getParentId(object) {
    try {
        var parentPaths = object["parent"].replace(" ", "");
    } catch (a) {
        alert(a);
    }

    return getParentName(parentPaths);
}

function getParentDiv(object) {
    var parentId = getParentId(object);

    var parentContainer = document.getElementById(parentId);

    return parentContainer;
}

function createResultDiv(testResult) {
    var testDiv = document.createElement("div"); 
    testDiv.className = "testDiv";
    $(testDiv).click(function (index) {
        showOrHideResults(this);
    });
    testDiv.title = getTestDivTooltip(testResult["description"], "Click for more info");
    testDiv.id = testResult["parent"] + "." + testResult["buildName"];
    testDiv.buildPath = testResult["parent"].replace(/[.]/g, JOB) + JOB + testResult["buildName"];
    testDiv.parentFolder = testResult["parent"];

    var statusDiv = document.createElement("div");
    statusDiv.className = "statusDiv";
    testDiv.appendChild(statusDiv);

    var innerTestDiv = document.createElement("div");
    innerTestDiv.className = "innerTestDiv";
    innerTestDiv.title = getTestDivTooltip(testResult["description"], "Click to hide");
    $(innerTestDiv).click(function (index) {
        isolateExpansionActivity = true;
        removeExpanded(this);
    });

    var testLabel = document.createElement("label");
    testLabel.className = "testLabel moveMainTestLabel";
    testLabel.innerText = testResult["buildName"];

    var buffer = document.createElement("label");
    buffer.className = "testLabel bold";
    buffer.innerText = testResult["buildName"];

    if (testResult["buildName"].length >= 50) {
        testLabel.className += " text-smallest";
        buffer.className += " text-smallest";
    }
    else if (testResult["buildName"].length >= 42) {
        testLabel.className += " text-smaller";
        buffer.className += " text-smaller";
    }
    else if (testResult["buildName"].length >= 34) {
        testLabel.className += " text-small";
        buffer.className += " text-small";
    }

    var leftBox = document.createElement("div");
    leftBox.className = "infoBox verticalTop";

    if (testResult["failedTests"] > 0) {
        var resultsIcon = getFailedTestsIcon(testResult["failedTests"]);
        resultsIcon.style = "right:10px";
        resultsIcon.title = "Failed scenarios";
        testDiv.appendChild(resultsIcon);
    }

    var resultBox = document.createElement("div");

    //resultBox
    var resultBoxClass = "resultBox bold ";
    var passed = testResult["passedTests"];
    var total = testResult["totalTests"];

    var passPercentage = passed / total;
    var failed = total - passed;

    switch (testResult["result"]) {
        case "FAILURE":
            if ((passPercentage < .9 && total >= 10) ||
                (total < 10 && failed > 1)) {
                resultBoxClass += "failure";
                statusDiv.className += " failure";
                testDiv.className += " failureDiv";
            }
            else {
                resultBoxClass += "flaky";
                statusDiv.className += " flaky";
                testDiv.className += " flakyDiv";
            }
            break;
        case "SUCCESS":
            resultBoxClass += "pass";
            statusDiv.className += " pass";
            testDiv.className += " successDiv";
            break;
        case "NOT RUN":
            resultBoxClass += "notRun";
            statusDiv.className += " notRun";
            testDiv.className += " notRunDiv";
            break;
        case "Building":
            resultBoxClass += "building";
            statusDiv.className += " building";
            testDiv.className += " buildingDiv";
            break;
        case "ERROR":
            resultBoxClass += " error";
            statusDiv.className += " error";
            testDiv.className += " noScenariosDiv";
            break;
        case "not found":
        default:
            resultBoxClass += "dangerOrange";
            statusDiv.className += " dangerOrange";
            testDiv.className += " notFoundDiv";
            break;
    }

    applyFiltering(testDiv);

    resultBox.className = resultBoxClass;
    resultBox.innerText = testResult["result"];

    var infoBox = document.createElement("div");
    infoBox.className = "infoBox";

    var durationLine = createInfoBoxLine("Duration", testResult["duration"]);
    var linkLine = createInfoBoxLine("Links", testResult["link"]);
    var runDateLine = createInfoBoxLine("Run Date", testResult["runDate"]);

    //passRate
    var passRate = document.createElement("div");
    var passPercentageText = (passPercentage * 100).toFixed(0) + "%";
    if (testResult["passedTests"] !== -1) {
        passRate.innerText = testResult["passedTests"] + "/" + testResult["totalTests"] + " (" + passPercentageText + ")";
    }
    passRate.className = "centerText";

    var buildButton = createBuildButton(testResult["parent"], testResult["buildName"], testResult["result"], testResult[BUILD_NUMBER]);

    infoBox.appendChild(durationLine);
    infoBox.appendChild(linkLine);
    infoBox.appendChild(runDateLine);

    leftBox.appendChild(resultBox);
    leftBox.appendChild(passRate);

    testDiv.appendChild(testLabel);

    innerTestDiv.appendChild(buffer);
    innerTestDiv.appendChild(leftBox);
    innerTestDiv.appendChild(infoBox);
    innerTestDiv.appendChild(buildButton);

    testDiv.appendChild(innerTestDiv);

    return testDiv;
}

function getTestDivTooltip(description, clickText) {
    var tooltip = document.createElement("span");

    var title = "";

    if (!description) {
        title = clickText;
    } else {
        title = description + "\n\n" + clickText;
    }

    return title;
}
function createInfoBoxLine(label, info) {
    var infoBoxLine = document.createElement("div");

    var infoBoxLabel = document.createElement("label");
    infoBoxLabel.innerText = label + ": ";

    var infoBoxValue;
    if (label === "Links") {
        if (info.nodeType === Node.ELEMENT_NODE) {
            infoBoxValue = info;
        } else {
            var infoSpan = document.createElement("span");
            infoSpan.innerText = info;
            infoBoxValue = infoSpan;
        }
    }
    else {
        infoBoxValue = document.createElement("span");
        infoBoxValue.innerText = info;
    }

    infoBoxLine.className = "infoBoxLine";

    infoBoxLine.appendChild(infoBoxLabel);
    infoBoxLine.appendChild(infoBoxValue);

    return infoBoxLine;
}

function createBuildButton(parent, buildName, result, buildNumber) {
    var buildButton = document.createElement("button");

    var path = createBuildPath(parent, buildName);

    if (result !== "Building") {
        buildButton.innerText = "Build";
        buildButton.id = parent + buildName + "buildbutton";
        buildButton.className = "buildButton";

        $(buildButton).click(function () {
            buildThis(path, buildName);
        });
    } else {
        buildButton.innerText = "STOP!";
        buildButton.id = parent + buildName + "buildbutton";
        buildButton.className = "buildButton error";

        $(buildButton).click(function () {
            stopThis(path, buildName, buildNumber);
        });
    }

    return buildButton;
}

function createIconBuildButton(parent, buildName, result, buildNumber) {
    var buildButton = createBuildButton(parent, buildName, result, buildNumber)

    buildButton.innerText = "";
    buildButton.className = "fa-button";

    if (result !== "Building") {
        buildButton.innerHTML = "<i class='fa-solid fa-circle-arrow-right' style='color: #25c322;'></i>";
        buildButton.id = parent + buildName + "buildbutton";
    } else {
        buildButton.innerHTML = "<i class='fa-solid fa-circle-stop' style='color: #e60a0a;'></i>";;
        buildButton.id = parent + buildName + "buildbutton";
    }

    return buildButton;
}

function createBuildPath(parent, buildName) {
    var jobPath = "";
    if (parent !== "main") {
        var folderName = parent.replace(" ", "%20").replace(/[.]/g, JOB);
        jobPath += folderName + JOB;
    }

    jobPath += buildName;

    //store buildPath in buildPaths
    var key = parent.replace(" ", "");
    if (buildPaths[key] === undefined) {
        buildPaths[key] = [];
    }
    buildPaths[key].push(jobPath);

    return jobPath;
}

function lastRefreshUpdate() {
    var refreshLabel = document.getElementById("refreshLabel");
    var dateTime = new Date();
    var hours = dateTime.getHours();
    var minutes = dateTime.getMinutes();
    var meridian = "";
    if (hours > 11) {
        meridian = "PM";
    } else {
        meridian = "AM";
    }

    if (hours > 12) {
        hours = hours - 12;
    }

    if (minutes < 10) {
        minutes = "0" + minutes;
    }

    let refreshText = "Last refresh: ";
    if (REFRESH_SHOWS_FULL_DATE_TIME) {
        var dayOfWeek = getDayOfTheWeekName(dateTime.getDay());
        var date = getMonthName(dateTime.getMonth()) + " " + dateTime.getDate() + " " + dateTime.getFullYear();
        refreshText += dayOfWeek + ", " + date + " ";
    }

    refreshText += hours + ":" + minutes + " " + meridian;

    refreshLabel.innerText = refreshText;
}

function convertToNaturalPhrase(input) {
    // Add spaces before uppercase letters
    let result = input.replace(/([a-z])([A-Z])/g, '$1 $2');

    // Ensure the first word is capitalized properly (e.g., Ae)
    result = result.charAt(0).toUpperCase() + result.slice(1);

    // Capitalize the first letter of each word after spaces
    result = result.replace(/\b[a-z]/g, char => char.toUpperCase());

    return result;
}

function getDayOfTheWeekName(dayOfTheWeekNumber) {
    switch (dayOfTheWeekNumber) {
        case 0:
            return "Sunday";
        case 1:
            return "Monday";
        case 2:
            return "Tuesday";
        case 3:
            return "Wednesday";
        case 4:
            return "Thursday";
        case 5:
            return "Friday";
        case 6:
            return "Saturday";
    }
}

function getMonthName(monthNumber) {
    switch (monthNumber) {
        case 0:
            return "January";
        case 1:
            return "February";
        case 2:
            return "March";
        case 3:
            return "April";
        case 4:
            return "May";
        case 5:
            return "June";
        case 6:
            return "July";
        case 7:
            return "August";
        case 8:
            return "September";
        case 9:
            return "October";
        case 10:
            return "November";
        case 11:
            return "December";
    }
}