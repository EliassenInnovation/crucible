$(document).ready(function () { setup(); });

function setup() {
    $('#controlDiv').removeAttr('hidden');
    $('#refreshButton').click(function () {
        statusRefresh();
    });

    commonSetup(statusRefresh);
}

function statusRefresh() {
    activateSpinner();
    getTestResults(restOfRefresh, true);
}

function restOfRefresh() {
    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.setOnLoadCallback(getTotals);
    getWorstOffender();
    getFails();
    getDisabledJobs();
    getBuildingJobs();
    getNewFails();

    lastRefreshUpdate();

    hideSpinner();
}

function setWorstOffender(testResult) {
    //create link
    testResult["link"] = CreateLinks(testResult);
    var testDiv = createResultDiv(testResult);
    $('#worstOffenderContent').html(testDiv);
}

function setFails(testResults) {
    var failsParentDiv = document.getElementById("fails");
    if (testResults.length > 0) {
        failsParentDiv.style.display = "inline-block";
        var failsDiv = document.getElementById("failsContent");
        failsDiv.innerHTML = "";

        var failedBuildsCount = testResults.length;
        $('#totalFailedBuildsSpan').text(" (" + failedBuildsCount + ")");

        testResults.forEach(function (testResult) {
            testResult["link"] = CreateLinks(testResult);
            var testDiv = createResultDiv(testResult);
            failsDiv.appendChild(testDiv);
        });
    } else {
        failsParentDiv.style.display = "none";
    }
}

function setNewFails(testResults) {
    var newFailsParentDiv = document.getElementById("newFails");
    if (testResults.length > 0) {
        newFailsParentDiv.style.display = "inline-block";
        var newFailsDiv = document.getElementById("newFailsContent");
        newFailsDiv.innerHTML = "";

        var newFailsCount = testResults.length;
        $('#totalNewFailsSpan').text(" (" + newFailsCount + ")");

        testResults.forEach(function (testResult) {
            testResult["link"] = CreateLinks(testResult);
            var testDiv = createResultDiv(testResult);
            newFailsDiv.appendChild(testDiv);
        });
    }
    else {
        newFailsParentDiv.style.display = "none";
    }
}

function setDisabledBuilds(testResults) {
    var disabledDiv = document.getElementById("disabledJobs");
    if (testResults.length > 0) {
        disabledDiv.style.display = "inline-block";
        var disabledContentDiv = document.getElementById("disabledJobsContent");
        disabledContentDiv.innerHTML = "";

        var disabledCount = testResults.length;
        $('#totalDisabledBuildsSpan').text(" (" + disabledCount + ")");

        testResults.forEach(function (testResult) {
            testResult["link"] = CreateLinks(testResult);
            var testDiv = createResultDiv(testResult);
            disabledContentDiv.appendChild(testDiv);
        });
    }
    else {
        
        disabledDiv.style.display = "none";
    }
}

function setBuildingBuilds(testResults) {
    var buildingDiv = document.getElementById("buildingJobs");
    if (testResults.length > 0) {
        buildingDiv.style.display = "inline-block";
        var buildingContentDiv = document.getElementById("buildingJobsContent");
        buildingContentDiv.innerHTML = "";

        var buildingCount = testResults.length;
        $('#totalBuildingBuildsSpan').text(" (" + buildingCount + ")");

        testResults.forEach(function (testResult) {
            testResult["link"] = CreateLinks(testResult);
            var testDiv = createResultDiv(testResult);
            buildingContentDiv.appendChild(testDiv);
        });
    }
    else {

        buildingDiv.style.display = "none";
    }
}

function drawChart(totals) {

    var successes = totals["successes"];
    var fails = totals["fails"];
    var others = totals["others"];
    var disabled = totals["disabled"];

    var data = google.visualization.arrayToDataTable([
        ['Builds', 'Number'],
        ['Passing', successes],
        ['Failing', fails],
        ['Disabled', disabled],
        ['Other',  others]
    ]);

    var options = {
        title: 'OVERALL BUILD PROGRESS',
        colors: ['rgb(146,221,150)', 'rgb(242,146,140)', 'orange', 'purple'],
        backgroundColor: '#f2f2f2',
        pieSliceTextStyle: { color: 'black' }
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart'));

    chart.draw(data, options);
}
