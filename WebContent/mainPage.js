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