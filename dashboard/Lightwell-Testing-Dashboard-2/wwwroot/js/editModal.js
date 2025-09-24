let currentCell;

$(document).ready(function () {
    
    // Open modal on cell click
    // See landing.js for click function on .editable TDs
    //$('.editable').on('click', function () {
    //    currentCell = $(this); // Save reference to the clicked cell
    //    const cellText = currentCell.text(); // Get the cell's text
    //    $('#editModalLabel').text(currentCell.attr('buildname'));
    //    $('#editBox').val(cellText); // Set the text in the modal's textarea
    //    $('#editModal').fadeIn(); // Show the modal
    //});

    // Save changes and close the modal
    $('#saveEdit').on('click', function () {
        const newText = $('#editBox').val(); // Get the edited text
        currentCell.attr('originaltext',newText);
        currentCell.html(span(embedJiraLinks(newText))); // Update the cell text
        $('#editModal').fadeOut(); // Hide the modal
        fireAction(newText); // Call a custom action
    });

    // Close the modal without saving
    $('.closeModal').on('click', function () {
        $('#editModal').fadeOut();
    });

    // Close the modal if the user clicks outside of it
    $(window).on('click', function (event) {
        if ($(event.target).is('#editModal')) {
            $('#editBox').val("");
            $('#editModal').fadeOut();
        }
    });

    // Custom action after saving
    function fireAction(updatedText) {
        console.log('Cell updated with text:', updatedText);
        updateDescription(currentCell.attr("buildpath"), updatedText)
    }
});

