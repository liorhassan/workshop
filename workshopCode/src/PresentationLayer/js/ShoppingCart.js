const window_name = "ShoppingCart";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn", {
        method: "POST",
        body: JSON.stringify({session_id: localStorage["session_id"]})
    })
    .then(response=>response.json())
    .then(updateNavBar);
    

     fetch("http://localhost:8080/tradingSystem/cart", {
        method: "POST",
        body: JSON.stringify({session_id: localStorage["session_id"]})
    })
         .then(response => response.json())
         .then(setItemsToCart)




    document.getElementById("editbtn").addEventListener("click", function () {
        // items[0].amount = 20
        // setItemsToCart(items)
        clearEditModalFields();
        document.getElementById("editModal").style.display = "block";
    })

    // When the user clicks on <span> (x), close the modal
    document.getElementsByClassName("close")[0].addEventListener("click", function () {
        document.getElementById("editModal").style.display = "none";
        document.getElementById("purchaseModal").style.display = "none";
    })

    document.getElementById("confirmEditbtn").addEventListener("click", function () {

        var productNameUpdate = document.getElementById("productNameUpdate").value;
        var storeNameUpdate = document.getElementById("storeNameUpdate").value;
        var quantityUpdate = document.getElementById("quantityUpdate").value;

        // TODO: send msg to server with the updated product item and get back the items in the cart (updated)
        fetch("http://localhost:8080/tradingSystem/editCart", {
            method: "POST",
            body: JSON.stringify({session_id: localStorage["session_id"], store:storeNameUpdate, product:productNameUpdate, amount:quantityUpdate })
        })
         .then(response => {
             if (response.ok) {
                 return response.json();
             } else {
                 return response.text();
             }
          })
         .then((responseMsg) => {
                 //setItemsToCart(items);
             if (responseMsg.SUCCESS) {
                fetch("http://localhost:8080/tradingSystem/cart", {
                    method: "POST",
                    body: JSON.stringify({session_id: localStorage["session_id"]})
                })
                         .then(response => response.json())
                         .then(setItemsToCart)
                  Swal.fire(
                        'Congratulations!',
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

    document.getElementById("purchasebtn").addEventListener("click", function () {
        // items[0].amount = 20
        // setItemsToCart(items)
        clearPurchaseModalFields();
        document.getElementById("purchaseModal").style.display = "block";
    })


    document.getElementById("confirmPurchasebtn").addEventListener("click", function () {
            var cardNumberUpdate = document.getElementById("cardNumberUpdate").value;
            var monthUpdate = document.getElementById("monthUpdate").value;
            var yearUpdate = document.getElementById("yearUpdate").value;
            var holderUpdate = document.getElementById("holderUpdate").value;
            var cvvUpdate = document.getElementById("cvvUpdate").value;
            var idUpdate = document.getElementById("idUpdate").value;
            var nameUpdate = document.getElementById("nameUpdate").value;
            var addressUpdate = document.getElementById("addressUpdate").value;
            var cityUpdate = document.getElementById("cityUpdate").value;
            var countryUpdate = document.getElementById("countryUpdate").value;
            var zipUpdate = document.getElementById("zipUpdate").value;

         fetch("http://localhost:8080/tradingSystem/purchaseCart", {
            method: "POST",
            body: JSON.stringify({session_id: localStorage["session_id"], cardNumber:cardNumberUpdate, month:monthUpdate, year:yearUpdate, holder:holderUpdate, cvv:cvvUpdate, id:idUpdate, name:nameUpdate, addr:addressUpdate, city:cityUpdate, country:countryUpdate, zip:zipUpdate})
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
                           'Congratulations!',
                           responseMsg.SUCCESS,
                           'success').then(setItemsToCart)
                 } else {
                     Swal.fire(
                        'OOPS!',
                        responseMsg,
                        'error').then(setItemsToCart)
                 }
             })

             document.getElementById("purchaseModal").style.display = "none";
    })

});

function clearEditModalFields() {
    document.getElementById("cardNumberUpdate").value = "";
    document.getElementById("storeNameUpdate").value = "";
    document.getElementById("quantityUpdate").value = "1";
}

function clearPurchaseModalFields() {
    document.getElementById("productNameUpdate").value = "";
    document.getElementById("monthUpdate").value = "";
    document.getElementById("yearUpdate").value = "";
    document.getElementById("holderUpdate").value = "";
    document.getElementById("cvvUpdate").value = "";
    document.getElementById("idUpdate").value = "";
    document.getElementById("idUpdate").value = "";
}


function setItemsToCart(items) {
    const shoppingCart = document.getElementById("shopCartContainer");
    shoppingCart.innerHTML = "";

    // TODO: get from server total price and put in var "total"
    fetch("http://localhost:8080/tradingSystem/cartTotalPrice", {
            method: "POST",
            body: JSON.stringify({session_id: localStorage["session_id"]})
        })
             .then(response => response.json())
             .then((data) => {
                document.getElementById("totalPriceContainer").innerHTML = "";
                var totalPricelbl = document.createElement("h5");
                totalPricelbl.append(document.createTextNode("Total Price: "+data.price));
                document.getElementById("totalPriceContainer").appendChild(totalPricelbl);

             })


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
    if (event.target == document.getElementById("purchaseModal")) {
            document.getElementById("purchaseModal").style.display = "none";
    }
}



