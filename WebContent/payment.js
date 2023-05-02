let cartList = jQuery("#cart_list");
let totalCost = document.getElementById("total_cost")

function handleCartData(dataString){
    let cartHtml = '';
    let movies = dataString['previousMovies']
    let title;
    let id;
    let tuple;
    for(let i = 0; i < movies.length; i++){
        tuple = movies[i].split("#");
        title = tuple[0];
        id = tuple[1];
        cartHtml += '<li id="${id}">Title: ' + title + ' | Id: ' + id + ' | Price: $10 '
            + '<button onclick="deleteMovie(\'${id}\', \'${title}\')">Remove Movie</button>' +'</li>';
    }
    // cartHtml += 'TOTAL PRICE: $' + movies.length * 10;
    cartList.append(cartHtml);

    totalCost.value = movies.length * 10;
    totalCost.innerHTML = 'TOTAL COST: $' + totalCost.value.toString();
}




$.ajax("api/cart", {
    method: "GET",
    dataType: "json",
    success: handleCartData
});


let pay_form = $("#pay_form");


function handlePayResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson);

    if (resultDataJson["status"] === "success") {
        window.location.replace("success.html");
    } else {

        $("#pay_error_message").text(resultDataJson["message"]);
    }
}


function submitPayForm(formSubmitEvent) {

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/pay", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: pay_form.serialize(),
            success: handlePayResult
        }
    );
}

// Bind the submit action of the form to a handler function
pay_form.submit(submitPayForm);
