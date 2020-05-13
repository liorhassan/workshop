document.addEventListener("DOMContentLoaded", function () {
    const items = [
        {
            "name": "paper",
            "price": 15,
            "store": "kravitz",
            "description": "ffdfd"
        },
        {
            "name": "pen",
            "price": 25,
            "store": "kravitz",
            "description": "ffdfd"
        },

        {
            "name": "pineapple",
            "price": 15,
            "store": "kravitz",
            "description": "ffdfd"
        },

        {
            "name": "apple",
            "price": 15,
            "store": "kravitz",
            "description": "ffdfd"
        }
    ]

    // fetch("localhost:8000/searchResults")
    //     .then(response => response.json())
    //     .then(setSearchResults)
    setSearchResults(items);


    const categories = ["Clothes","Food","Tech","Gardening"]

    // fetch("localhost:8000/searchResults")
    //     .then(response => response.json())
    //     .then(setSearchResults)
    setCateegories(categories);

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
    function setCateegories(categories) {

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







