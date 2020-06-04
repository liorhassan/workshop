const window_name = "DiscountView";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn", {
        method: "POST",
        body: JSON.stringify({session_id: localStorage["session_id"]})
    })
    .then(response=>response.json())
    .then(updateNavBar);
})
function addNewDiscount(data) {

    data.store = document.getElementById("storeName").value;
    fetch("http://localhost:8080/tradingSystem/addDiscount", {
        method: "POST",
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(data => {
            if (data.SUCCESS) {
                Swal.fire(
                    'Congratulations!',
                    data.SUCCESS,
                    'success')
            } else {
                Swal.fire(
                    'OOPS...',
                    data,
                    'error')
            }
        })
}

function addBasketAmount() {
    const amount = document.getElementById("basketAmountNumber").value;
    const percent = document.getElementById("basketAmountPercentNumber").value;

    const data = {
        type: "onBasket",
        subtype: "onProductsAmount",
        amount: amount,
        percent: percent
    }
    addNewDiscount(data);
}

function addBasketPrice() {
    const price = document.getElementById("basketPriceNumber").value;
    const percent = document.getElementById("basketPricePercentNumber").value;

    const data = {
        type: "onBasket",
        subtype: "onCost",
        price: price,
        percent: percent
    }
    addNewDiscount(data);
}

function addProductReveal() {
    const name = document.getElementById("revealedProductName").value;
    const percents = document.getElementById("revealedDiscountPercents").value;

    const data = {
        type: "onProduct",
        subtype: "revealed",
        productName: name,
        percent: percents
    }

    addNewDiscount(data);
}

function addProductConditional() {
    const name = document.getElementById("conditionalProductName").value;
    const amount = document.getElementById("conditionalAmount").value
    const percents = document.getElementById("conditionalDiscountPercents").value;
    const radioBtn = document.getElementsByName("productCondition");

    const data = {
        type: "onProduct",
        subtype: "conditional",
        productName: name,
        amount: amount,
        percent: percents,
        onProducts: radioBtn[0].checked ? true : false,
        onNextProduct: radioBtn[1].checked ? true : false,
    }

    addNewDiscount(data);
}

function resetActiveTabsButThis(clickedTab) {
    if (!clickedTab.classList.contains("active")) {
        const allActive = document.getElementsByClassName("active")
        const relevantActive = Array.prototype.filter.call(allActive, function (currElement) {
            return currElement.getAttribute("name") != 'menu-nav';
        });

        for (let i = 0; i < relevantActive.length; i++) {
            const element = relevantActive[i];

            if (element != clickedTab && element.getAttribute("name") != "") {
                element.classList.remove("active")
            }
        }

        const allShown = document.getElementsByClassName("show")
        while (allShown.length > 0) {
            const element = allShown[0];

            if (element != clickedTab) {
                element.classList.remove("show")
            }
        }
    }
}