document.addEventListener("DOMContentLoaded", function () {


    fetch("http://localhost:8080/tradingSystem/allProducts")
         .then(response => response.json())
         .then(setSearchResults)
   
    fetch("http://localhost:8080/tradingSystem/allCategories")
         .then(response => response.json())
         .then(setCategories)
   
    document.getElementsByClassName("close")[0].addEventListener("click", function () {
    document.getElementById("editModal").style.display = "none";
    })

    document.getElementById("search-button").addEventListener("click", function() {
        var name = document.getElementById("product-name").value;
        var selected = document.getElementById("category-drop1").selectedIndex;
        var category = (selected ==  0) ? "" : document.getElementById("category-drop1").options[selected].text;
        var keyWords = document.getElementById("key-words").value.split(" ");
        if (keyWords == "")
            keyWords = [];
    
        fetch("http://localhost:8080/tradingSystem/search", {
            method: "POST",
            body: JSON.stringify({name: name, category: category, keywords: keyWords})
        })
        .then(response => {
            if (response.ok) {
                response.json().then(setSearchResults);
            } else {
                response.text().then(responseMsg=>{
                    Swal.fire(
                        'OOPS!',
                        responseMsg,
                        'error')
                })
            }
        })
        
    })

    document.getElementById("filter-button").addEventListener("click", function() {
        var min_price = document.getElementById("min-price").value;
        var max_price = document.getElementById("max-price").value;
        var selected = document.getElementById("category-drop2").selectedIndex;
        var category = (selected ==  0) ? "" : document.getElementById("category-drop2").options[selected].text;
        var temp = JSON.stringify({maxPrice: max_price, minPrice: min_price, category: category});
        fetch("http://localhost:8080/tradingSystem/filter", {
            method: "POST",
            body: JSON.stringify({maxPrice: max_price, minPrice: min_price, category: category})
        })
        .then(response => {
            if (response.ok) {
                response.json().then(setSearchResults);
            } else {
                response.text().then(responseMsg=>{
                    Swal.fire(
                        'OOPS!',
                        responseMsg,
                        'error')
                })
            }
        })
    })
});


function setSearchResults(items) {
    const numOfItems = document.getElementById("num-of-items");
    numOfItems.innerHTML = "";
    numOfItems.append(document.createTextNode(items.length));

    const searchResults = document.getElementById("search-results");

    searchResults.innerHTML = "";

    items.forEach(currItem => {
        const element = document.createElement("li");
        element.classList.add("list-group-item");
        element.classList.add("d-flex");
        element.classList.add("justify-content-between");
        element.classList.add("lh-condensed");

        element.addEventListener("click",function(){
            showPopUp(currItem);
        });
        
        const element_div = document.createElement("div");
        
        const product_name = document.createElement("h6");
        product_name.classList.add("my-0");
        product_name.append(document.createTextNode(currItem.name));

        const store_name = document.createElement("small");
        store_name.classList.add("text-muted");
        store_name.append(document.createTextNode(currItem.store));

        element_div.appendChild(product_name);
        element_div.appendChild(store_name);

        const price = document.createElement("span");
        price.classList.add("text-muted");
        price.append(document.createTextNode("$" + currItem.price));
        
        element.appendChild(element_div);
        element.appendChild(price);
        
        searchResults.appendChild(element);
    })
}

function setCategories(categories) {

    const category_drop1 = document.getElementById("category-drop1");
    const category_drop2 = document.getElementById("category-drop2");
    category_drop1.innerHTML = "";
    category_drop2.innerHTML = "";

    const main_item1 = document.createElement("option");
    main_item1.selected = true;
    main_item1.append(document.createTextNode("Category"));
    category_drop1.appendChild(main_item1);
    const main_item2 = document.createElement("option");
    main_item2.selected = true;
    main_item2.append(document.createTextNode("Category"));
    category_drop2.appendChild(main_item2);

    var val = 1;
    categories.forEach(currCat=>{
        const item1 = document.createElement("option");
        item1.value=val;
        item1.append(document.createTextNode(currCat));
        category_drop1.appendChild(item1);
        const item2 = document.createElement("option");
        item2.value=val;
        item2.append(document.createTextNode(currCat));
        category_drop2.appendChild(item2);
        val++;
    })
}

function showPopUp(item){
    document.getElementById("model-product-name").innerHTML = "Name: " + item.name;
    document.getElementById("product-store").innerHTML = "Store: " + item.store;
    document.getElementById("product-price").innerHTML = "Price: " + item.price;
    document.getElementById("product-description").innerHTML = "Description: " + item.description;
    document.getElementById("product-amount").value = 0;

    document.getElementById("add-to-cart").addEventListener("click", function() {
        var product_name = item.name;
        var product_store = item.store;
        var product_amount = document.getElementById("product-amount").value;
    
        fetch("http://localhost:8080/tradingSystem/addToShoppingBasket", {
            method: "POST",
            body: JSON.stringify({store: product_store, product:product_name, amount: product_amount})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })

        document.getElementById("editModal").style.display = "none";

    })

    document.getElementById("editModal").style.display = "block";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == document.getElementById("editModal")) {
        document.getElementById("editModal").style.display = "none";
    }
}
