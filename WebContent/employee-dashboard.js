let star_form = $("#star_form");

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

//---------------------------------

let movie_form = $("#movie_form");

function submitMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/add-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: movie_form.serialize(),
            success: function(){
                alert('successfully added movie');

            },
            error: function(){
                alert('something went wrong, you may have tried to insert a movie that already exists')
            }
        }
    );
}

// Bind the submit action of the form to a handler function
movie_form.submit(submitMovieForm);



//---------------------------------

let metaDiv = jQuery("#metadata");
function handleMetaResult(data) {
    let htmlToAdd = '';
    console.log(data);
    let pastTable = '';
    for (let i = 0; i < data.length; i++) {
        if (data[i]['tableName'] == '' || data[i]['tableName'] != pastTable){
            htmlToAdd += "<h3>" + data[i]['tableName'] + "</h3>";

        }
        htmlToAdd += "<p>-" + data[i]['columnName'] + " " + data[i]['columnType'] + "</p>";
        pastTable = data[i]['tableName'];

    }
    metaDiv.append(htmlToAdd);

}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
