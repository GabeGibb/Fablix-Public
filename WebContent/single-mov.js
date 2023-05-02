/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


function addToCart(id, title){
    $.ajax("api/cart?id=" + id + "&title=" + title, {
        method: "POST",
    });
    alert("added to cart!");
}


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>");



    let rowHTML = "";


    let genres = "";
    for (let j = 0; j < resultData[0]["movie_genres"].length; j++){
        // genres += resultData[0]["movie_genres"][j] + ", ";
        genres += '<a href="movies.html?genre='+ resultData[0]["movie_genres"][j] + '">' +
            resultData[0]["movie_genres"][j] + ", " + '</a>';
    }
    rowHTML += "<p>Genres: " + genres.slice(0, -2) + "</p>";

    let stars = "";
    for (let j = 0; j < resultData[0]["movie_stars"].length; j++){
        stars += '<a href="single-star.html?id=' + resultData[0]["movie_stars_id"][j] + '">' +
            resultData[0]["movie_stars"][j] + ", " + '</a>';
    }
    rowHTML += "</a><p>Stars: " + stars + "</p>";
    rowHTML += "<p>Rating: " + resultData[0]["movie_rating"] + "</p>";

    rowHTML += "<p>" + "<button onclick='addToCart(" + '"' + resultData[0]['movie_id'] + '", "' +
        resultData[0]['movie_title'] + '"' + ")'>"  + "Add To Cart" + "</button></p>";
    // Append the row created to the table body, which will refresh the page
    starInfoElement.append(rowHTML);

    console.log("handleResult: populating movie table from resultData");


}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});