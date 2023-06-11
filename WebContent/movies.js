
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

function changeN(){
    let url2 = window.location.href;
    // url2 = url2.replace('movies.html', 'api/movies')
    let nValue = document.getElementById("N").value;

    let newUrl = new URL(url2)
    newUrl.searchParams.set('n', nValue)
    newUrl.searchParams.set('start', '0')
    // url2 += '&n=' + nValue
    location.href = newUrl.toString();
}

function prevPage(){
    let url2 = window.location.href;
    // url2 = url2.replace('movies.html', 'api/movies')
    let nValue = getParameterByName("n")
    let start = getParameterByName("start")
    if (nValue == null || nValue == ''){
        nValue = 25;
    }
    if (start == null || nValue == ''){
        start = 0;
    }

    let newUrl = new URL(url2)
    let newStart = Number(start) - Number(nValue)
    if (newStart < 0){
        newStart = 0
    }
    newUrl.searchParams.set('start', newStart.toString())
    location.href = newUrl.toString();
}

function nextPage(){
    let url2 = window.location.href;
    // url2 = url2.replace('movies.html', 'api/movies')
    let nValue = getParameterByName("n")
    let start = getParameterByName("start")
    if (nValue == null || nValue == ''){
        nValue = 25;
    }
    if (start == null || nValue == ''){
        start = 0;
    }


    let newUrl = new URL(url2)
    let newStart = Number(start) + Number(nValue)
    newUrl.searchParams.set('start', newStart.toString())
    location.href = newUrl.toString();

}


function sortMovies(){
    let url2 = window.location.href;
    // url2 = url2.replace('movies.html', 'api/movies')
    let sortDir1 = document.getElementById('sortDirTitle').value
    let sortDir2 = document.getElementById('sortDirRating').value
    let sortBy = document.getElementById('sortBy').value

    let newUrl = new URL(url2)

    newUrl.searchParams.set('ordert', sortDir1)
    newUrl.searchParams.set('orderr', sortDir2)
    newUrl.searchParams.set('first', sortBy)
    location.href = newUrl.toString();
}


function addToCart(id, title){
    $.ajax("api/cart?id=" + id + "&title=" + title, {
        method: "POST",
    });
    alert("added to cart!");
}



function handleStarResult(resultData) {

    console.log(resultData);

    let starTableBodyElement = jQuery("#movie_table_body");

    if (resultData[0]["new_url"] != ''){
        window.location.href = resultData[0]["new_url"];
    }
    for (let i = 1; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        let genres = "";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_genres"].length); j++){
            // genres += resultData[i]["movie_genres"][j] + ", ";
            genres += '<a href="movies.html?genre='+ resultData[i]["movie_genres"][j] + '">' +
                resultData[i]["movie_genres"][j] + ", " + '</a>';
        }
        rowHTML += "<th>" + genres.slice(0, -2) + "</th>";

        let stars = "";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_stars"].length); j++){
            stars += '<a href="single-star.html?id=' + resultData[i]["movie_stars_id"][j] + '">' +
                resultData[i]["movie_stars"][j] + ", " + '</a>';
        }
        rowHTML += "<th>" + stars.slice(0, -2) + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        rowHTML += "<th>" + "<button onclick='addToCart(" + '"' + resultData[i]['movie_id'] + '", "' +
           resultData[i]['movie_title'] + '"' + ")'>"  + "Add To Cart" + "</button></th>";


        rowHTML += "</tr>";



        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let url2 = window.location.href;
url2 = url2.replace('movies.html', 'api/movies')
// console.log(url2)
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: url2, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});