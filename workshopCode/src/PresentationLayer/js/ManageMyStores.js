var activeStore;
var activeProducts;
document.addEventListener("DOMContentLoaded", function () {
    const stores = [
        {
            "name": "Castro",
            "type": "Owner",
            "options": ["Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Mango",
            "type": "Manager",
            "options": ["Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Delta",
            "type": "Owner",
            "options": ["Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Zara",
            "type": "Manager",
            "options": ["View Purchasing History"]
        }
    ]

    // fetch("localhost:8000/myStores")
    //     .then(response => response.json())
    //     .then(setMyStores)
    setMyStores(stores);

    document.getElementById("new-store-button").addEventListener("click",function(){
        showPopUp("Open Store");
    });

    initOpenStoreModel();
    
    initAddManagerModel();

    initAddOwnerModel();

    initRemoveManagerModel()

    //initManageSupplyModel();
    

    fetch("http://localhost:8080/tradingSystem/allCategories")
         .then(response => response.json())
         .then(setCategories)

    // When the user clicks on <span> (x), close the modal
    var x_buttons = document.getElementsByClassName("close");
    for (let x_button of x_buttons) {
        x_button.addEventListener("click", function () {
            var modals = document.getElementsByClassName("modal");
            for (let modal of modals) {
                modal.style.display = "none";
            }
        });
    }
});

function initOpenStoreModel() {   
    document.getElementById("confirm-open-store-btn").addEventListener("click",function(){
        var store_name = document.getElementById("StoreNameInput").value;
        var store_description  = document.getElementById("StoreDescInput").value;
        fetch("/tradingSystem/openNewStore", {
            method: "POST",
            body: JSON.stringify({store: store_name, description: store_description})
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
        document.getElementById("StoreNameInput").value = "";
        document.getElementById("StoreDescInput").value = "";
        document.getElementById("openStoreModal").style.display = "none";
    })
}

function initAddManagerModel() {   
    document.getElementById("add-manager-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("new-manager-name").value;
        fetch("http://localhost:8080/tradingSystem/addStoreManager", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
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
        document.getElementById("new-manager-name").value = "";
        document.getElementById("addManagerModal").style.display = "none";
    })
}

function initAddOwnerModel() {   
    document.getElementById("add-owner-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("new-owner-name").value;
        fetch("http://localhost:8080/tradingSystem/addStoreOwner", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
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

        document.getElementById("new-owner-name").value = "";
        document.getElementById("addOwnerModal").style.display = "none";
    })
}

function initRemoveManagerModel() {   
    document.getElementById("remove-manager-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("remove-manager-name").value;
        fetch("http://localhost:8080/tradingSystem/removeStoreManager", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
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

        document.getElementById("remove-manager-button").value = "";
        document.getElementById("removeManagerModal").style.display = "none";
    })
}

function updateSupplyProducts(products){
    activeProducts = products;
    var option_menu = document.getElementById("store-product-list");
    option_menu.innerHTML = "";
    products.forEach(prod=>{
        const option = document.createElement("option");
        option.value = prod.name;
        option.append(document.createTextNode(prod.name));
        option_menu.appendChild(option);
    })
}

function updatePurchaseHistory(history){
    var historyList = document.getElementById("history-list");
    historyList.innerHTML = "<br>";
    //history.innerHTML = "";
    history.forEach(purchase=>{
        purchase.forEach(prod=>{
            const element = document.createElement("li");
            element.classList.add("list-group-item");
            element.classList.add("d-flex");
            element.classList.add("justify-content-between");
            element.classList.add("lh-condensed");
            const element_div = document.createElement("div");
            const product_name = document.createElement("h6");
            product_name.classList.add("my-0");
            product_name.append(document.createTextNode(prod.name));
            const amount = document.createElement("small");
            amount.classList.add("text-muted");
            amount.append(document.createTextNode("Amount: "+prod.amount));
            element_div.appendChild(product_name);
            element_div.appendChild(amount);
            const price = document.createElement("span");
            price.classList.add("text-muted");
            price.append(document.createTextNode("$" + prod.price));
            element.appendChild(element_div);
            element.appendChild(price); 
            historyList.appendChild(element);
        })
        const break_element = document.createElement("br");
        historyList.appendChild(break_element);
    })
}

function setMyStores(stores) {
    const numOfStores = document.getElementById("num-of-stores");
    numOfStores.innerHTML = "";
    numOfStores.append(document.createTextNode(stores.length));

    const stores_list = document.getElementById("stores-list");

    stores_list.innerHTML = "";

    stores.forEach(currStore => {
        const element = document.createElement("li");
        element.classList.add("list-group-item");
        element.classList.add("d-flex");
        element.classList.add("justify-content-between");
        element.classList.add("lh-condensed");
        
        const element_div = document.createElement("div");
        
        const store_name = document.createElement("h6");
        store_name.classList.add("my-0");
        store_name.append(document.createTextNode(currStore.name));

        const title = document.createElement("small");
        title.classList.add("text-muted");
        title.append(document.createTextNode(currStore.type));

        element_div.appendChild(store_name);
        element_div.appendChild(title);

        const actions = document.createElement("span");

        const action_div = document.createElement("div");
        action_div.classList.add("input-group-append");

        const action_button = document.createElement("button");
        action_button.append(document.createTextNode("Actions"));
        action_button.classList.add("btn");
        action_button.classList.add("btn-outline-secondary");
        action_button.classList.add("dropdown-toggle");
        action_button.type = "button";
        action_button.setAttribute("data-toggle", "dropdown");
        action_button.setAttribute("aria-haspopup", "true");
        action_button.setAttribute("aria-expanded", "false");

        const action_dropdown = document.createElement("div");
        action_dropdown.id = "menu_dropDown";
        action_dropdown.classList.add("dropdown-menu");

        currStore.options.forEach(currAction=>{
            const a = document.createElement("a");
            a.addEventListener("click",function(){
                activeStore = currStore;
                showPopUp(currAction);
            })
            a.classList.add("dropdown-item");
            a.append(document.createTextNode(currAction));

            action_dropdown.appendChild(a);  
        })

        action_div.appendChild(action_button);
        action_div.appendChild(action_dropdown);
        actions.appendChild(action_div);
        
        element.appendChild(element_div);
        element.appendChild(actions);
        
        stores_list.appendChild(element);
    })
}


function setCategories(categories) {

    const category_drop1 = document.getElementById("update-product-category");
    category_drop1.innerHTML = "";

    const main_item1 = document.createElement("option");
    main_item1.selected = true;
    main_item1.append(document.createTextNode("Category"));
    category_drop1.appendChild(main_item1);

    var val = 1;
    categories.forEach(currCat=>{
        const item1 = document.createElement("option");
        item1.value=val;
        item1.append(document.createTextNode(currCat));
        category_drop1.appendChild(item1);
    })
}

actionToModel={
    "Open Store":"openStoreModal",
    "Add Manager":"addManagerModal",
    "Add Owner":"addOwnerModal",
    "Remove Manager":"removeManagerModal",
    "Edit Permissions":"editPermissionsModal",
    "Manage Supply":"manageSupplyModal",
    "View Purchasing History":"viewPurchaseHistoryModal"
}

function showPopUp(action){
    if(action == "View Purchasing History"){
        fetch("http://localhost:8080/tradingSystem/storePurchaseHistory", {
            method: "POST",
            body: JSON.stringify({store:activeStore.name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(updatePurchaseHistory)
    }
    if(action == "Manage Supply"){
        fetch("http://localhost:8080/tradingSystem/getStoreProducts", {
            method: "POST",
            body: JSON.stringify({store:activeStore.name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(updateSupplyProducts)
    }
    document.getElementById(actionToModel[action]).style.display = "block";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    var models = this.document.getElementsByClassName("modal");
    for(let model of models){
        //model.style.display = "none";
    }
}




