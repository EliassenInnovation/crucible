var activeTabHref;
var expandedDiv;
var leafFolderId;

function buildThis(jobPath, buildName) {

    var apiUrl = "./home/TriggerBuild";
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: { "buildPath": jobPath },
        success: function (result) {
            if (result.success) {
                showNotification(buildName + " triggered");

                if (REFRESH_INTERVAL !== 0) {
                    setTimeout(function () { refreshFunction }, REFRESH_INTERVAL);
                }
            } else {
                alert("Building " + buildName + " failed!");
            }
        },
        error: function (result) {
            alert(result);
        }
    });
}

function yesNoDialog(message, yesCallback, noCallback, imageSource) {
    var dialog = $("<div>").dialog({
        modal: true,
        show: true,
        buttons: {
            "Yes": function () {
                $(this).dialog("close");
                yesCallback();
            },
            "No": function () {
                $(this).dialog("close");
                if (noCallback !== undefined) {
                    noCallback();
                }
            }
        }
    });

    var dialogInnerHTML = "";

    if (imageSource !== undefined) {
        dialogInnerHTML += "<img src='" + imageSource + "' alt='image'> ";
    }

    dialogInnerHTML += message;

    dialog.html(dialogInnerHTML);
}

//function stopThis(jobPath, buildName) {
//    yesNoDialog("Are you sure you want to stop " + buildName + "?", function () {
//        var apiUrl = "./jobs/stopjob";
//        $.ajax({
//            url: apiUrl,
//            method: "GET",
//            dataType: "json",
//            data: { "buildPath": jobPath },
//            success: function (result) {
//                if (result.success) {
//                    showNotification(buildName + " stopped");

//                    if (REFRESH_INTERVAL !== 0) {
//                        setTimeout(function () { refreshFunction }, REFRESH_INTERVAL);
//                    }
//                } else {
//                    alert("Building " + buildName + " failed!");
//                }
//            },
//            error: function (result) {
//                alert(result);
//            }
//        });
//    }, function () {
//        // Do something if the user cancels
//    }, "./images/heman/breakstaff.gif");
//}

function stopThis(jobPath, buildName, buildNumber) {
    var answer = confirm("Are you sure you want to stop " + buildName + "?");
    if (answer) {
        var apiUrl = "/jobs/stopjob";
        $.ajax({
            url: apiUrl,
            method: "POST",
            dataType: "json",
            data: {
                "jobPath": jobPath,
                "buildNumber": buildNumber
            },
            success: function (response) {
                if (response.ok) {
                    showNotification(buildName + " stopped");
                    if (REFRESH_INTERVAL !== 0) {
                        setTimeout(function () { refreshFunction() }, REFRESH_INTERVAL);
                    }
                } else {
                    alert("Stopping " + buildName + " failed! " + 
                          (response.error || "Unknown error"));
                }
            },
            error: function (xhr, status, error) {
                // This handles network errors or invalid JSON responses
                alert("Request failed: " + error);
            }
        });
    }
}

function build(paths) {
    var totalJobs = buildPaths[currentFolder].length;
    var runJobs = 0;
    var apiUrl = "./home/TriggerBuild";

    paths.forEach(function (jobPath) {
        $.ajax({
            url: apiUrl,
            method: "GET",
            dataType: "json",
            data: { "buildPath": jobPath },
            success: function (result) {
                if (!result.success) {
                    success = false;
                }
                runJobs++;
                notifyAllInActiveTabSuccess(runJobs, totalJobs, success, getCurrentFolderChildName())
            },
            error: function (result) {
                success = false;
                runJobs++;
            }
        });
    });

}

function buildAllInActiveTab() {
    var success = true;

    var level = $('#currentFolderDiv').text();

    var answer = confirm("Are you sure you want to build all of the " + level + " builds?");

    if (answer) {
        var currentFolder = getCurrentFolderName();
        if (buildPaths[currentFolder] != undefined) {
            triggerBuilds(buildPaths[currentFolder], getCurrentFolderChildName());

        } else {
            alert("No builds to build at " + level);
        }
    }
}

function triggerBuilds(buildPaths, folderName) {
    var success = true;
    var apiUrl = "./home/TriggerBuilds";

    $.ajax({
        url: apiUrl,
        method: "POST",
        dataType: "json",
        data: { "buildPaths": buildPaths },
        success: function (result) {
            if (!result.success) {
                success = false;
            }
            showNotification(result.jobsSuccessfullyTriggered + " builds from " + folderName + " triggered successfully");
        },
        error: function (result) {
            showNotification("At least one error triggering " + folderName + " builds. See Jenkins for details");
        }
    });
}

function buildAll(paths, buildFolderName) {
    var success = true;
    var totalJobs = paths.length;
    var runJobs = 0;
    var apiUrl = "./home/TriggerBuild";

    paths.forEach(function (jobPath) {
        $.ajax({
            url: apiUrl,
            method: "GET",
            dataType: "json",
            data: { "buildPath": jobPath },
            success: function (result) {
                if (!result.success) {
                    success = false;
                }
                runJobs++;
                notifyAllInActiveTabSuccess(runJobs, totalJobs, success, buildFolderName)
            },
            error: function (result) {
                success = false;
                runJobs++;
            }
        });
    });
}

function notifyAllInActiveTabSuccess(runJobs, totalJobs, success, level) {
    if (runJobs < totalJobs) {
        return;
    }

    if (success) {
        showNotification(level + " builds triggered successfully");
    }
    else {
        showNotification("At least one error triggering " + level + " builds. See Jenkins for details");
    }
}

function getTestResults(callbackFunction, shouldRefresh) {
    activateSpinner();
    if (shouldRefresh === undefined) {
        shouldRefresh = false;
    }

    var apiUrl = "./home/GetTestResults";

    var sortValue = document.getElementById("sortOrder").value;

    $.ajax({
        url: apiUrl,
        method: "POST",
        dataType: "json",
        data: {
            sort: sortValue,
            refresh: shouldRefresh
        },
        success: function (result) {
            //sets global value
            testResults = result;
            callbackFunction();
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function showLeaf(folderId) {
    if (folderId.includes('.')) {
        var parentId = getParentName(folderId);
        showLeaf(parentId);
    }
    var folder = document.getElementById(folderId + "Folder");
    if (folder !== null) {
        folder.click();
    }
}

function getWorstOffender() {
    var apiUrl = "./status/GetWorstOffender";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            setWorstOffender(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getFails() {
    var apiUrl = "./status/GetFails";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            setFails(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getDisabledJobs() {
    var apiUrl = "./status/GetDisabledJobs";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            setDisabledBuilds(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getBuildingJobs() {
    var apiUrl = "./status/GetBuildingJobs";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            setBuildingBuilds(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getTotals() {
    var apiUrl = "./status/GetTotals";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            drawChart(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getNewFails() {
    var apiUrl = "./status/GetNewFails";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "GET",
        dataType: "json",
        data: {
        },
        success: function (result) {
            setNewFails(result);
        },
        error: function (result) {
            console.log(JSON.stringify(result));
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function updateDescription(buildpath, description) {
    var apiUrl = "./summary/UpdateDescription";
    activateSpinner();
    $.ajax({
        url: apiUrl,
        method: "POST",
        dataType: "json",
        data: {
            "buildPath": buildpath,
            "updatedDescription": description
        },
        success: function (result) {
            showNotification("Build description updated");
        },
        error: function (result) {
            showNotification("Build description update failed");
        },
        complete: function () {
            hideSpinner();
        }
    });
}

function getActiveTab() {
    var activeTabItem;
    var activeTabContainer = document.getElementsByClassName("tabActive")[0];
    if (activeTabContainer !== undefined) {
        activeTabItem = activeTabContainer.getElementsByClassName("tabLink")[0];
    }

    if (activeTabItem === undefined) {
        activeTabItem = document.getElementsByClassName('tabLink')[0];
    }

    return activeTabItem;
}

function showNotification(message, skipThis) {
    if (skipThis === null || !skipThis) {
        var notificationDiv = document.createElement("div");
        notificationDiv.className = "notificationDiv";

        notificationDiv.innerText = message;

        document.getElementsByTagName("body")[0].append(notificationDiv);

        var timeOut = setTimeout(function () {
            notificationDiv.style.top = "0px";
            var timeOut2 = setTimeout(function () {
                notificationDiv.style.top = "-100px";
                var timeOut3 = setTimeout(function () {
                    document.getElementsByTagName("body")[0].removeChild(notificationDiv);
                    refreshFunction();
                }, 2000);
            }, 3000);
        }, 1000);
    }
}

function showOrHideResults(testDiv) {
    if (!isolateExpansionActivity) {

        if (expandedDiv !== undefined && $(expandedDiv).hasClass("testDivExpanded")) {
            removeExpanded(expandedDiv);
        }

        $(testDiv).addClass("testDivExpanded");
        $(testDiv).children('.statusDiv').addClass('resultsHidden');
        $(testDiv).children('.moveMainTestLabel').addClass('resultsHidden');
        expandedDiv = testDiv;
    }
    isolateExpansionActivity = false;
}

function removeExpanded(div, currentDiv) {
    if (currentDiv === null || (div !== currentDiv)) {
        if ($(div).hasClass('innerTestDiv')) {
            $(div).parent().removeClass('testDivExpanded');
            $(div).parent().children('.statusDiv').removeClass('resultsHidden');
            $(div).parent().children('.moveMainTestLabel').removeClass('resultsHidden');
        } else {
            $(div).removeClass('testDivExpanded');
            $(div).children('.statusDiv').removeClass('resultsHidden');
            $(div).children('.moveMainTestLabel').removeClass('resultsHidden');
        }
    }
}

function buildAllFailingActiveTab() {
    var success = true;

    var answer = confirm("Are you sure you want to build all of the non-passing " + getCurrentFolderName() + " builds?");

    if (answer) {
        var currentFolder = document.getElementById($(getCurrentFolder()).attr('id'));

        var failingDivs = [];

        var testDivs = [];

        var xpath = "//div[contains(@id,'" + currentFolder.id + "')]//div/div[(contains(@class,'flaky') or contains(@class,'failure')) and not(contains(@class,'resultBox'))]/..";

        var testDivResults = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        for (var x = 0; x < testDivResults.snapshotLength; x++) {
            testDivs.push(testDivResults.snapshotItem(x));
        }

        for (var x = 0; x < testDivs.length; x++) {
            var statusDiv = $(testDivs[x]).children('.statusDiv');
            if ($(statusDiv).hasClass("failure") ||
                $(statusDiv).hasClass("flaky") ||
                $(statusDiv).hasClass("dangerOrange") ||
                $(statusDiv).hasClass("notRun") ||
                $(statusDiv).hasClass("error")) {
                failingDivs.push(testDivs[x]);
            }
        }

        var totalJobs = failingDivs.length;
        var runJobs = 0;

        //var currentFolderName = getCurrentFolderName();
        //var folderBuildPaths = buildPaths[currentFolderName];

        var failingBuildPaths = [];
        failingDivs.forEach(function (div) {
            var buildPath = div.buildPath;

            var currentFolderName = div.parentFolder;

            var folderBuildPaths = buildPaths[currentFolderName];
            try {
                folderBuildPaths.forEach(function (path) {
                    if (path.includes(buildPath)) {
                        failingBuildPaths.push(path);
                    }
                });
            } catch (e) {
                console.log(e);
            }
        });

        var apiUrl = "./home/TriggerBuild";
        failingBuildPaths.forEach(function (jobPath) {
            $.ajax({
                url: apiUrl,
                method: "GET",
                dataType: "json",
                data: { "buildPath": jobPath },
                success: function (result) {
                    if (!result.success) {
                        success = false;
                    }
                    runJobs++;
                    notifyAllInActiveTabSuccess(runJobs, totalJobs, success, getCurrentFolderChildName())
                },
                error: function (result) {
                    success = false;
                    runJobs++;
                }
            });
        });

    }
}

function getFolderNameFromDiv(div) {
    var parts = div.id.split('.');
    var folderName = "";
    for (var x = 0; x < parts.length - 1; x++) {
        if (folderName !== "") {
            folderName += ".";
        }

        folderName += parts[x];
    }

    return folderName;
}

function setTotals() {
    var passPercentage = totalPassed / totalTests;

    var totalDiv = document.getElementById("totalDiv");
    var progress = document.getElementById("progress");

    if (passPercentage <= .94) {
        progress.className = " failureProgress";
    }
    else if (passPercentage < 1) {
        progress.className = " flakyProgress";
    }
    else {
        progress.className = " passProgress";
    }

    progress.value = totalPassed;
    progress.max = totalTests;
    $(progress).attr('data-label', totalPassed + "/" + totalTests);

    var displayValue = getDisplayPercentage(passPercentage);
    totalDiv.innerText = displayValue;
}

function updateTabSuccess(collectionName) {
    var tabPassed = totalPassedByTab[collectionName];
    var tabTotal = totalTestsByTab[collectionName];

    if (tabTotal > 0) {
        var tabProgress = document.getElementById("tabProgress");
        var tabSuccessRatioTotal = document.getElementById("tabTotalDiv");

        if (tabPassed) {
            tabProgress.value = tabPassed;
        } else {
            tabProgress.value = 0;
        }
        if (tabTotal) {
            tabProgress.max = tabTotal;
        } else {
            tabTotal = 1
        }

        var tabPercentage = tabPassed / tabTotal;

        if (tabPercentage <= .94) {
            tabProgress.className = " failureProgress";
        }
        else if (tabPercentage < 1) {
            tabProgress.className = " flakyProgress";
        }
        else {
            tabProgress.className = " passProgress";
        }

        $(tabProgress).attr('data-label', tabPassed + "/" + tabTotal);

        var displayValue = getDisplayPercentage(tabPercentage);

        tabSuccessRatioTotal.innerText = displayValue;

    } else {
        $('#folderProgress').hide();
    }
}

function updateWeatherIcon(collectionName) {
    var folderPassed = totalPassedByTab[collectionName];
    var folderTotal = totalTestsByTab[collectionName];

    if (folderTotal > 0) {
        var folderPercentage = folderPassed / folderTotal;
        let folderFailed = folderTotal - folderPassed;
        var weatherImageName;
        var healthIcon;
        var healthClass;

        if (folderPercentage <= .94) {
            weatherImageName = "health-00to19.png";
            healthIcon = getFailedTestsIcon(folderFailed);
            healthClass = "";
        }
        else if (folderPercentage < 1) {
            weatherImageName = "health-20to39.png";
            healthIcon = getFailedTestsIcon(folderFailed);
            healthClass = "";
        }
        else {
            weatherImageName = "health-80plus.png";
            healthIcon = '<i class="fa-solid fa-check fa-2xl" style="color: #92dd96;" title="No failures"></i>';
            healthClass = "healthIcon";
        }

        var displayValue = getDisplayPercentage(folderPercentage);

        let cleanedName = collectionName.replace(/[.]/g, "\\.");

        let tabWeatherIconId = '#' + cleanedName + "WeatherIcon";
        if (HEALTH_ICONS === "WEATHER") {
            let weatherIconImagePathRoot = "./images/health/";
            $(tabWeatherIconId).attr('src', weatherIconImagePathRoot + weatherImageName);
        } else if (HEALTH_ICONS === 'CHECKS') {
            $(tabWeatherIconId).html(healthIcon).addClass(healthClass);
        }
    }

    //this is a child of something
    if (collectionName.includes('.')) {
        let parts = collectionName.split('.');
        let rehydratedName = "";
        for (var x = 0; x < parts.length - 1; x++) {
            if (rehydratedName.length > 0) {
                rehydratedName += '.';
            }
            rehydratedName += parts[x];
        }

        updateWeatherIcon(rehydratedName);
    }
}

function getFailedTestsIcon(failedCount) {
    let icon = document.createElement('div');
    icon.style.display = 'inline-block;'
    icon.className = 'healthIcon healthIconDigits';
    icon.innerText = failedCount;
    icon.title = failedCount + " failing tests in this folder and subfolders"
    return icon;
}

function getDisplayPercentage(passPercentage) {
    var displayValue = "";
    if (!isNaN(passPercentage)) {
        displayValue = (passPercentage * 100).toFixed(0) + "%";
    }
    else {
        displayValue = "No Tests";
    }

    return displayValue;
}

function getCurrentFolderName() {
    var currentFolder = getCurrentFolder();
    return getFolderName(currentFolder);
}

function getFolderName(folder) {
    var currentFolderName;
    if ($(folder).attr('id') === testResultSpace.id) {
        currentFolderName = "main";
    } else {
        currentFolderName = $(folder).attr('id');
    }
    return currentFolderName;
}

function getCurrentFolder() {
    var currentFolder = foldersTraversed[foldersTraversed.length - 1];
    return currentFolder;
}

function getCurrentFolderChildName() {
    var currentFolderName = getCurrentFolderName();
    return getChildName(currentFolderName);
}

function getChildName(path) {
    var splitPath = path.split('.');
    return splitPath[splitPath.length - 1];
}

function updateCurrentFolderSideBarText() {
    var breadCrumbs = document.getElementById("breadCrumbs");
    if (breadCrumbs === undefined || breadCrumbs === null) {
        var currentFoldeDiv = document.getElementById('currentFolderDiv');
        var breadCrumbs = document.createElement('div');
        breadCrumbs.id = "breadCrumbs";
        currentFoldeDiv.appendChild(breadCrumbs);
    } else {
        breadCrumbs.innerHTML = "";
    }

    var arrowHtml = "&nbsp;<i class='fas fa-caret-right'></i>&nbsp;";

    for (const folder of foldersTraversed) {
        var crumbAnchor = document.createElement('a');
        crumbAnchor.className = "breadCrumb";

        var folderName = getFolderName(folder);
        if (folderName === "main") {
            if (folder.attr('id') === 'testResultSpace') {
                folderName = TOP_LEVEL_NAME;
            } else {
                folderName = "Main";
            }
        }

        var bob = getChildName(folderName);
        var folderId = folder.attr('id');

        crumbAnchor.innerHTML = bob;
        crumbAnchor.id = folderId + "BreadCrumb";
        crumbAnchor.parentId = folderId;

        breadCrumbs.appendChild(crumbAnchor);

        anchorId = crumbAnchor.id.replace(/[.]/g, "\\.");

        $('#' + anchorId).attr('parentId', crumbAnchor.parentId)
        $('#' + anchorId).click(function (e) { crumbClick($(this).attr('parentId'), $(this)); });

        if (folder !== foldersTraversed[foldersTraversed.length - 1]) {
            var arrowSpan = document.createElement("span");
            arrowSpan.innerHTML = arrowHtml;
            breadCrumbs.appendChild(arrowSpan);
        }
    }

    crumbAnchor.className += " leaf";
    leafFolderId = folderId;
}

function crumbClick(crumbId, element) {
    if (element.attr('class').includes('leaf')) {
        var folderId = element.id.replace('BreadCrumb', 'Folder');
        var folder = $('#' + folderId);
        folder.click();
    } else {
        while (foldersTraversed[foldersTraversed.length - 1].attr('id') !== crumbId) {
            showPreviousLevel();
        }
    }
}

var spinnerCount = 0;
function activateSpinner() {
    spinner.style.display = "block";
    spinnerModal.style.display = "block";
    spinnerCount++;
}

function hideSpinner() {
    spinnerCount--;
    if (spinnerCount < 1) {
        spinner.style.display = "none";
        spinnerModal.style.display = "none";
        spinnerCount = 0;
    }
}

function removeFilter(filterName) {
    var indexOfFilterName = filters.indexOf(filterName);
    if (indexOfFilterName !== -1) {
        filters.splice(indexOfFilterName, 1);
    }
    var filterNameClass = filterName + 'Div';
    $('.' + filterNameClass).each(function () {
        $(this).removeClass('hide');
    })
}

function addFilter(filterName) {
    filters.push(filterName);

    var filterNameClass = filterName + 'Div';
    $('.' + filterNameClass).each(function () {
        $(this).addClass('hide');
    })
}

function applyFiltering(div) {
    filters.forEach((filterName) => {
        if (div.className.includes(filterName)) {
            div.className += " hide";
        }
    });
}

function hideFilterBox() {
    filterContainer.addClass('filterContainerHide');
}

function recursivelyAddTotals() {
    collections.forEach(function (collectionName) {
        //if (collectionName.includes('.')) {
        //    let parts = collectionName.split('.');
        //    let rehydratedParentName = "";
        //    for (var x = 0; x < parts.length - 1; x++) {
        //        if (rehydratedParentName.length > 0) {
        //            rehydratedParentName += '.';
        //        }
        //        rehydratedParentName += parts[x];
        //        checkIfTabTotalValuesAreDefined(rehydratedParentName);
        //        totalPassedByTab[rehydratedParentName] += totalPassedByTab[collectionName];
        //        totalTestsByTab[rehydratedParentName] += totalTestsByTab[collectionName];
        //    }
        //}
    });
}

