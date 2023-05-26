let genresDiv = jQuery("#genres-div");
function handleResult(data) {
    let htmlToAdd = '';
    console.log(data);

    for (let i = 0; i < data.length; i++) {
        htmlToAdd += '<a href="movies.html?genre=' + data[i]['genre'] + '">' + data[i]['genre'] + '</a>'
        htmlToAdd += ' '
    }
    genresDiv.append(htmlToAdd);

}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/mainPage", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


//AUTOCOMPLETE

let cachedMovies = {};

function handleLookup(query, doneCallback) {
    if (query.length < 3){
        return;
    }
    console.log("autocomplete initiated")


    if (cachedMovies[query] != undefined){
        console.log('Getting results from cache')
        handleLookupAjaxSuccess(cachedMovies[query], query, doneCallback, true)
    }
    else{
        console.log("sending AJAX request to backend Java Servlet")
        console.log('Getting results from database')
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/movies?n=10&full-text=true&title=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback, false)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }



}


function handleLookupAjaxSuccess(data, query, doneCallback, saved) {


    //LET EM KNOW IF IT WAS CACHED OR NOT

    let movies = [];
    if (!saved){
        console.log("lookup ajax successful")
        for(let i = 1; i < data.length; i++){
            movies.push({'value': data[i]['movie_title'], 'data': data[i]['movie_id']})
        }
        cachedMovies[query] = movies;
    }
    else{
        movies = data
    }

    console.log(movies)

    doneCallback( { suggestions: movies } );
}


function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log(suggestion)
    location.href = "single-movie.html?id=" + suggestion['data']
}


// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)

    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});



function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})






