let star_form = $("#star_form");


function handleStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson);
    if (resultDataJson["status"] === "success") {
        alert('Star Added!')
    } else {
        alert('Something went wrong, try again')
    }
}


function submitStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/add-star", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: function(){
                alert('successfully added star');
            },
            error: function(){
                alert('something went wrong, try again!');
            }
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitStarForm);

