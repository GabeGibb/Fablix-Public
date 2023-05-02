let cartList = jQuery("#cart_list");
let totalCost = document.getElementById("total_cost")
function deleteMovie(id, title){
    let movie = document.getElementById(id);
    let numMovie = Number(movie.value);

    movie.remove();
    totalCost.value -= 10 * numMovie;
    totalCost.innerHTML = 'TOTAL COST: $' + totalCost.value.toString();

    $.ajax("api/cart?id=" + id + "&title=" + title + "&remove=true", {
        method: "POST",
    });
}

function decrement(id, title){
    let movie = document.getElementById(id);
    let numMovie = Number(movie.value);


    if (numMovie > 0){
        $.ajax("api/cart?id=" + id + "&title=" + title + "&remove=true", {
            method: "POST",
        });
        numMovie -= 1
        totalCost.value -= 10;
        totalCost.innerHTML = 'TOTAL COST: $' + totalCost.value.toString();
        if(numMovie == 0){
            movie.remove();
        }
    }
    movie.value = numMovie;

    movie.innerHTML = movie.innerHTML.slice(0, -1);
    movie.innerHTML += movie.value;

}

function increment(id, title){
    let movie = document.getElementById(id);
    let numMovie = Number(movie.value);
    totalCost.value += 10;
    totalCost.innerHTML = 'TOTAL COST: $' + totalCost.value.toString();

    if (numMovie > 0){
        $.ajax("api/cart?id=" + id + "&title=" + title, {
            method: "POST",
        });
        numMovie += 1
    }
    movie.value = numMovie;
    movie.innerHTML = movie.innerHTML.slice(0, -1);
    movie.innerHTML += movie.value;
}


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

        let movie = document.getElementById(id);
        if (movie == null){
            cartHtml += '<li id="' + id + '" value="1">Title: ' + title + ' | Id: ' + id + ' | Price: $10 | Num: '
                + '<button onclick="deleteMovie(\'' + id + '\', \'' + title + '\')">Remove Movie</button>'
                + '<button onclick="decrement(\'' + id + '\', \'' + title + '\')">-</button>'
                + '<button onclick="increment(\'' + id + '\', \'' + title + '\')">+</button>1' + '</li>';

        }
        else{
            movie.value += 1;
            movie.innerHTML = movie.innerHTML.slice(0, -1);
            movie.innerHTML += movie.value;
        }


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