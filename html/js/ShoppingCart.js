document.addEventListener("DOMContentLoaded", function () {
//    const items = [
//        {
//            "name": "paper",
//            "price": 15,
//            "store": "kravitz",
//            "amount": 3
//        },
//        {
//            "name": "pen",
//            "price": 25,
//            "store": "kravitz",
//            "amount": 3
//        },
//
//        {
//            "name": "pineapple",
//            "price": 15,
//            "store": "kravitz",
//            "amount": 3
//        },
//
//        {
//            "name": "apple",
//            "price": 15,
//            "store": "kravitz",
//            "amount": 3
//        }
//    ]

     fetch("http://localhost:8080/tradingSystem/cart")
         .then(response => response.json())
         .then(setItemsToCart)
//    setItemsToCart(items)

    document.getElementById("editbtn").addEventListener("click", function () {
        // items[0].amount = 20
        // setItemsToCart(items)
        clearEditModalFields();
        document.getElementById("editModal").style.display = "block";
    })

    // When the user clicks on <span> (x), close the modal
    document.getElementsByClassName("close")[0].addEventListener("click", function () {
        document.getElementById("editModal").style.display = "none";
    })

    document.getElementById("confirmEditbtn").addEventListener("click", function () {

        var productNameUpdate = document.getElementById("productNameUpdate").value;
        var storeNameUpdate = document.getElementById("storeNameUpdate").value;
        var quantityUpdate = document.getElementById("quantityUpdate").value;

        // TODO: send msg to server with the updated product item
        //       and get back the items in the cart (updated)
//        items[0].amount = quantityUpdate;
//        items[0].name = productNameUpdate;
//        items[0].store = storeNameUpdate;
        // ******************************************************

        document.getElementById("editModal").style.display = "none";
        setItemsToCart(items);
    })

    document.getElementById("purchasebtn").addEventListener("click", function () {
        // TODO: send msg to server to purchase cart
    })
        

});

function clearEditModalFields() {
    document.getElementById("productNameUpdate").value = "";
    document.getElementById("storeNameUpdate").value = "";
    document.getElementById("quantityUpdate").value = "1";
}

function setItemsToCart(items) {
    const shoppingCart = document.getElementById("shopCartContainer");
    shoppingCart.innerHTML = "";

    
    // TODO: get from server total price and put in var "total"
    document.getElementById("totalPriceContainer").innerHTML = "";
    var total = "100$";
    var totalPricelbl = document.createElement("h5");
    totalPricelbl.append(document.createTextNode("Total Price: "+total));
    document.getElementById("totalPriceContainer").appendChild(totalPricelbl);


    items.forEach(currItem => {
        const cardItem = document.createElement("div")
        cardItem.classList.add("card");

        const cardBodyItem = document.createElement("div")
        cardBodyItem.classList.add("card-body");

        const storeLabel = document.createElement("label")
        storeLabel.append(document.createTextNode("store name: " + currItem.store))
        const nameLabel = document.createElement("label")
        nameLabel.append(document.createTextNode("Product name: " + currItem.name))
        const amountLabel = document.createElement("label")
        amountLabel.append(document.createTextNode("amount: " + currItem.amount))
        const priceLabel = document.createElement("label")
        priceLabel.append(document.createTextNode("price: " + currItem.price))

        cardBodyItem.appendChild(storeLabel);
        cardBodyItem.appendChild(nameLabel);
        cardBodyItem.appendChild(amountLabel);
        cardBodyItem.appendChild(priceLabel);

        cardItem.appendChild(cardBodyItem);

        shoppingCart.appendChild(cardItem);
    }
    );
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == document.getElementById("editModal")) {
        document.getElementById("editModal").style.display = "none";
    }
}



