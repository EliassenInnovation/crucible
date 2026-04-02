$(document).ready(function () { setup(); });

var collectionMap = [];
var testResultSpace = document.getElementById("testResultSpace");
var sidebar = document.getElementById("sideBar");
var spinner = document.getElementById("spinner");
var spinnerModal = document.getElementById("spinnerModal");
var filterContainer = $('#filterContainer');

var totalPassed = 0;
var totalTests = 0;
var totalPassedByTab = [];
var totalTestsByTab = [];

var timer;

var foldersTraversed = [];

function setup() {
    commonSetup(indexRefresh);
    $('#controlDiv').removeAttr('hidden');
    $('#currentFolderDiv').removeAttr('hidden');
    $('#currentFolderDiv').css("color", CRUMB_COLOR);
    $('#controlDiv').css("color", CRUMB_COLOR);

    $('#refreshButton').click(function () {
        indexRefresh();
    });

    $('#buildAll').click(function () {
        buildAllInActiveTab();
    });

    $('#buildFailing').click(function () {
        buildAllFailingActiveTab();
    });

    $('#filter').click(function () {
        if (filterContainer.hasClass('filterContainerHide')) {
            filterContainer.removeClass('filterContainerHide');
        } else {
            hideFilterBox();
        }
    })

    $('.filter').each(function (filter) {
        $(this).click(function (thisFilter) {
            if ($(this).hasClass('filterSelected')) {
                $(this).removeClass('filterSelected');
                addFilter($(this).attr('value'));
            } else {
                $(this).addClass('filterSelected');
                removeFilter($(this).attr('value'));
            }
        });
    });

    $('#upALevel').click(function () {
        showPreviousLevel();
    })
    foldersTraversed.push($('#testResultSpace'));
    $('#upALevel').hide();
    $('#folderProgress').hide();

    $('#spinnerModal').click(function () { /*intercepts clicks*/ });

    getTestResults(setupDashboard);

    //populateCurrentFolderPlace();
    updateCurrentFolderSideBarText();
}

function setupDashboard() {
    createDashboard();
    foldersTraversed = [];
    foldersTraversed.push($('#testResultSpace'));
    showLeaf(leafFolderId);
}

function showPreviousLevel() {
    var totalFoldersTraversed = foldersTraversed.length;
    foldersTraversed[totalFoldersTraversed - 1].addClass('resultsHidden')
    foldersTraversed.splice(totalFoldersTraversed - 1,1);
    foldersTraversed[totalFoldersTraversed - 2].removeClass('resultsHidden');
    $(foldersTraversed[totalFoldersTraversed - 2]).find('.folderSection').each(function (index, childElement) {
        $(this).removeClass('resultsHidden');
    });

    if (foldersTraversed.length === 1) {
        $('#upALevel').hide();
        $('#folderProgress').hide();
    } else {
        $('#folderProgress').show();
    }

    updateCurrentFolderSideBarText();

    updateTabSuccess(foldersTraversed[totalFoldersTraversed - 2].attr('id'));
}

function createDashboard() {
    totalPassedByTab = [];
    totalTestsByTab = [];

    collections = getCollections();

    collections.forEach(function (collection) {

        if (collection !== undefined) {
            creatTestResultsCollectionSection(collection)
        }
    });

    if (testResults != undefined) {
        testResults.buildResults.forEach(function (testResult) {
            createTestResultSection(testResult);
        });
    }

    recursivelyAddTotals();

    collections.forEach(function (collection) {
        updateWeatherIcon(collection);
    });
}

function creatTestResultsCollectionSection(name) {
    var collectionName = name.replace(" ", "");

    var testResultsCollection = document.getElementById(collectionName);

    if (testResultsCollection === null) {
        var testResultCollection = document.createElement("div");
        var collectionDivId = collectionName;
        testResultCollection.id = collectionDivId;
        testResultCollection.className = "folderDiv resultsHidden";
        totalPassedByTab[collectionName] = testResults.totalsByParent[collectionName].successes;
        totalTestsByTab[collectionName] = testResults.totalsByParent[collectionName].total;

        if (!collectionName.includes(".")) {
            createFolderSection(collectionName, testResultSpace, collectionName);
        } else {
            var parent = document.getElementById(getParentName(collectionName));
            if (parent === null) {
                //need to create the ancestors
                createAncestorCollections(collectionName);
                parent = document.getElementById(getParentName(collectionName));
            }
            createFolderSection(getChildName(collectionName), parent, collectionName);
        }

        testResultSpace.appendChild(testResultCollection);
    }
}

function createAncestorCollections(levelName) {
    if (levelName.includes(".")) {
        createAncestorCollections(getParentName(levelName));
        }
    creatTestResultsCollectionSection(levelName);
}

function createFolderSection(folderName, parent, folderId) {
    //create link

    var fullFolderId = folderId + "Folder";
    var folderSectionTemp = document.getElementById(fullFolderId)
    if (folderSectionTemp === null) {

        var folderSection = document.createElement("div");
        folderSection.id = fullFolderId;

        folderSection.className = "folderSection";
        folderSection.title = "Click for more info";

        let folderWeatherIcon = null;

        if (HEALTH_ICONS === 'WEATHER') {
            folderWeatherIcon = document.createElement("img");
            folderWeatherIcon.className = "weatherIcon";
        }
        else if (HEALTH_ICONS === 'CHECKS') {
            folderWeatherIcon = document.createElement("span");
            //folderWeatherIcon.className = "healthIcon";
        }

        folderWeatherIcon.id = folderId + "WeatherIcon";
        folderSection.appendChild(folderWeatherIcon);

        var folderLabel = document.createElement("label");
        folderLabel.className = "folderLabel";
        folderLabel.innerText = folderName;

        folderSection.appendChild(folderLabel);

        var folderIconDiv = document.createElement("div");
        folderIconDiv.className = "testTypeIconContainer";
        var folderIcon = document.createElement("img");
        folderIcon.src = "./images/folder.svg"
        folderIcon.title = "folder";
        folderIcon.className = "testTypeIcon";
        folderIconDiv.appendChild(folderIcon);
        folderSection.appendChild(folderIconDiv);

        try {
            parent.appendChild(folderSection);
        } catch (a) {
            alert(a);
        }

        addFolderClick($('#' + folderSection.id));
    }
}

function addFolderClick(element) {
    $(element).click(function () {
        var resultSpaceId = element.attr('id').replace('Folder', '').replace(/[.]/g, "\\.");

        $('.folderDiv').each(function (index) {
            $(this).addClass('resultsHidden');
        });
        $('.folderSection').each(function (index) {
            $(this).addClass('resultsHidden');
        })

        var currentFolder = $('#' + resultSpaceId);

        currentFolder.removeClass('resultsHidden');

        var alreadyLogged = false;
        foldersTraversed.forEach(function (folder) {
            if (folder.attr('id') === currentFolder.attr('id')) {
                alreadyLogged = true;
            }
        })

        if (!alreadyLogged) {
            foldersTraversed.push(currentFolder);
            $('#upALevel').show();
            $('#folderProgress').show();
        }

        $(currentFolder).find('.folderSection').each(function (index, childElement) {
            $(this).removeClass('resultsHidden');
            addFolderClick($(this));
        });

        updateTabSuccess(resultSpaceId.replace(/\\/g, ""));
        updateCurrentFolderSideBarText();
    });
}

function checkIfTabTotalValuesAreDefined(id) {
    if(totalPassedByTab[id] === undefined) {
        totalPassedByTab[id] = 0;
    }
    if (totalTestsByTab[id] === undefined) {
        totalTestsByTab[id] = 0;
    }
}

function getCollections() {
    var _collections = [];

    if (testResults != undefined) {
        testResults.buildResults.forEach(function (testResult) {
            var parent = testResult["parent"];
            if (!_collections.includes(parent)) {
                _collections.push(parent);
                if (buildPaths[parent] !== undefined) {
                    buildPaths[parent.replace(" ", "")] = [];
                }
            }
        });
    }

    return _collections;
}

//function populateCurrentFolderPlace() {
//    var currentFolderPlace = document.createElement("div");
//    currentFolderPlace.id = "currentFolderDiv";
//    currentFolderPlace.className = "currentFolder";

//    sidebar.appendChild(currentFolderPlace);
//}

function indexRefresh() {
    activateSpinner();

    clear();
    getTestResults(setupDashboard, true);

    lastRefreshUpdate();
    
    hideSpinner();
}

function clear() {
    try {
        $('#tabs').remove();
    } catch (e) {
        //not really  an error, but needs to be caught
    }
    testResultSpace.innerHTML = "";

    buildPaths = {};
    totalPassed = 0;
    totalTests = 0;
}
