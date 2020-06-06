const window_name = "PolicyView";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn", {
        method: "POST",
        body: JSON.stringify({session_id: localStorage["session_id"]})
    })
    .then(response=>response.json())
    .then(updateNavBar);
})

const CATEGORY = "category"
const PRODUCT = "product"
const COMP = "composition"
const SIMPLE = "Simple"

let result = {};
let rootElementType;
let totalComp;
let prevSelect = {}
let allDiscounts = [{ discountId: 1, discountName: "A" }, { discountId: 2, discountName: "AB" }, { discountId: 3, discountName: "C" }, { discountId: 4, discountName: "D" }];
function getDiscounts() {
    const store = document.getElementById("storeName").value;

    if (!store || store.length == 0) {
        Swal.fire(
            'Forgot something?',
            'You must specify a store name to get discounts',
            'warning')
    } else {
        fetch("http://localhost:8080/tradingSystem/getDiscounts", {
            method: "POST",
            body: JSON.stringify({ store: store })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text();
                }
            })
            .then(data => {
                if (typeof (data) == "string") {
                    Swal.fire(
                        'OOPS..',
                        data,
                        'error')

                } else if (data.length == 0) {
                    Swal.fire(
                        'Unfortunatly',
                        "There are no discounts",
                        'warning')
                } else {
                    allDiscounts = data;
                    document.getElementById("allPolicyDiv").style.display = 'inline';
                }
            })
    }
}

function submitPolicy() {
    result = {}
    if (rootElementType == COMP) {
        result.type = "compose";
        const rator = document.getElementById("rator" + 1)
        result.operator = rator.options[rator.selectedIndex].value;
        const rand1 = document.getElementById(COMP + 1 + "rand1")
        result.operand1 = getOperand(rand1.options[rand1.selectedIndex].value, rand1.dataset.child)
        const rand2 = document.getElementById(COMP + 1 + "rand2")
        result.operand2 = getOperand(rand2.options[rand2.selectedIndex].value, rand2.dataset.child)
    }

    result.store = document.getElementById("storeName").value;

    fetch("http://localhost:8080/tradingSystem/addDiscountPolicy", {
        method: "POST",
        body: JSON.stringify(result)
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return response.text();
        }
    }).then(data => {
        if (data.SUCCESS) {
            Swal.fire(
                'Congratulations!',
                data.SUCCESS,
                'success').then(() => {
                    window.location.href = "http://localhost:8080/html/ManageMyStores.html";
                })
        } else {
            Swal.fire(
                'OOPS...',
                data,
                'error')
        }
    })
}

function getOperand(type, childIndex) {
    const currOperand = {};
    if (type == COMP) {
        currOperand.type = "Comp";
        const rator = document.getElementById("rator" + childIndex)
        currOperand.operator = rator.options[rator.selectedIndex].value;
        const rand1 = document.getElementById(COMP + childIndex + "rand1")
        currOperand.operand1 = getOperand(rand1.options[rand1.selectedIndex].value, rand1.dataset.child)
        const rand2 = document.getElementById(COMP + childIndex + "rand2")
        currOperand.operand2 = getOperand(rand2.options[rand2.selectedIndex].value, rand2.dataset.child)
    }
    else {
        currOperand.type = "simple";
        currOperand.discountId = type;
    }

    return currOperand;
}

function createCompStatement(compCounter, parentIndex, parentID) {
    if (parentID == "Root") {
        document.getElementById("policy-content").innerHTML = "";
        rootElementType = COMP;
        totalComp = compCounter;
    }
    if (compCounter != 1 || document.getElementById("compositionDiv1") == null) {
        const compDiv = document.createElement("div");
        compDiv.setAttribute("id", COMP + "Div" + compCounter);
        compDiv.className = "policyDiv input-group mb-3";

        // Add label header
        const labelHeader = document.createElement("label");
        labelHeader.append(document.createTextNode("Composition #" + compCounter + " of " + parentID))
        compDiv.appendChild(labelHeader);

        const contentDiv = document.createElement("div");
        contentDiv.className = "input-group mb-3";
        compDiv.appendChild(contentDiv);

        // Create content
        // operator
        const ratorDiv = document.createElement("div");
        ratorDiv.className = "input-group-prepend";
        const ratorTextSpan = document.createElement("span");
        ratorTextSpan.className = "input-group-text";
        ratorTextSpan.append(document.createTextNode("rator"))
        ratorDiv.appendChild(ratorTextSpan);
        contentDiv.appendChild(ratorDiv);

        const ratorinput = document.createElement("select");
        ratorinput.setAttribute("id", "rator" + compCounter);

        const ratorOption1 = document.createElement("option");
        ratorOption1.setAttribute("value", "IF_THEN");
        ratorOption1.appendChild(document.createTextNode("IF_THEN"));
        const ratorOption2 = document.createElement("option");
        ratorOption2.setAttribute("value", "AND");
        ratorOption2.appendChild(document.createTextNode("AND"));
        const ratorOption3 = document.createElement("option");
        ratorOption3.setAttribute("value", "XOR");
        ratorOption3.appendChild(document.createTextNode("XOR"));

        ratorinput.appendChild(ratorOption1)
        ratorinput.appendChild(ratorOption2)
        ratorinput.appendChild(ratorOption3)
        contentDiv.appendChild(ratorinput);

        // rand 1
        const rand1Div = document.createElement("div");
        rand1Div.className = "input-group-prepend";
        const rand1TextSpan = document.createElement("span");
        rand1TextSpan.className = "input-group-text";
        rand1TextSpan.append(document.createTextNode("rand1"))
        rand1Div.appendChild(rand1TextSpan);
        contentDiv.appendChild(rand1Div);

        const rand1Select = document.createElement("select");
        rand1Select.setAttribute("id", COMP + compCounter + "rand1");
        rand1Select.setAttribute("name", "rand1");
        rand1Select.dataset.parent = parentIndex;
        rand1Select.dataset.counter = compCounter;

        const rand1DefaultOption = document.createElement("option");
        rand1DefaultOption.setAttribute("selected", true)
        rand1DefaultOption.appendChild(document.createTextNode("- Choose operand -"));
        rand1Select.appendChild(rand1DefaultOption);
        allDiscounts.forEach(discount => {
            const rand1Option = document.createElement("option");
            rand1Option.setAttribute("value", discount.discountId);
            rand1Option.appendChild(document.createTextNode(discount.discountName));
            rand1Select.appendChild(rand1Option);
        });


        const rand1CompOption = document.createElement("option");
        rand1CompOption.setAttribute("value", COMP);
        rand1CompOption.appendChild(document.createTextNode(COMP));
        rand1Select.appendChild(rand1CompOption);

        rand1Select.addEventListener("focus", (elem) => {
            prevSelect[elem.target.id] = elem.target.value
        })

        rand1Select.addEventListener("change", (elem) => {
            operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
            prevSelect[elem.target.id] = elem.target.value
        })

        contentDiv.appendChild(rand1Select);

        // rand 2
        const rand2Div = document.createElement("div");
        rand2Div.className = "input-group-prepend";
        const rand2TextSpan = document.createElement("span");
        rand2TextSpan.className = "input-group-text";
        rand2TextSpan.append(document.createTextNode("rand2"))
        rand2Div.appendChild(rand2TextSpan);
        contentDiv.appendChild(rand2Div);

        const rand2Select = document.createElement("select");
        rand2Select.setAttribute("id", COMP + compCounter + "rand2");
        rand2Select.setAttribute("name", "rand2");
        rand2Select.dataset.parent = parentIndex;
        rand2Select.dataset.counter = compCounter;

        const rand2DefaultOption = document.createElement("option");
        rand2DefaultOption.setAttribute("selected", true)
        rand2DefaultOption.appendChild(document.createTextNode("- Choose operand -"));
        rand2Select.appendChild(rand2DefaultOption);
        allDiscounts.forEach(discount => {
            const rand2Option = document.createElement("option");
            rand2Option.setAttribute("value", discount.discountId);
            rand2Option.appendChild(document.createTextNode(discount.discountName));
            rand2Select.appendChild(rand2Option);
        });


        const rand2Option3 = document.createElement("option");
        rand2Option3.setAttribute("value", COMP);
        rand2Option3.appendChild(document.createTextNode(COMP));

        rand2Select.appendChild(rand2Option3);

        rand2Select.addEventListener("focus", (elem) => {
            prevSelect[elem.target.id] = elem.target.value
        })

        rand2Select.addEventListener("change", (elem) => {
            operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
            prevSelect[elem.target.id] = elem.target.value
        })

        contentDiv.appendChild(rand2Select);

        document.getElementById("policy-content").appendChild(compDiv);

        totalComp++
    }
}

function operandTypeChanged(operandType, prevType, compIndex, parentID) {
    if (prevType != operandType) {
        const parentIndex = document.getElementById(parentID).dataset.child;
        const currElem = document.getElementById(prevType + "Div" + parentIndex);
        if (currElem && prevType == COMP) {
            removeDescendants(prevType, parentIndex)
        }

        document.getElementById(parentID).dataset.child = totalComp;
        document.getElementById(parentID).dataset.childType = operandType;

        if (operandType == COMP) {
            createCompStatement(totalComp, compIndex, parentID);
        }
    }
}

function removeDescendants(type, index) {
    const ran1Elem = document.getElementById(type + index + "rand1");
    const ran2Elem = document.getElementById(type + index + "rand2");

    checkAndRemove(ran1Elem.dataset.childType, ran1Elem.dataset.child)
    checkAndRemove(ran2Elem.dataset.childType, ran2Elem.dataset.child)

    const elemToDelete = document.getElementById(type + "Div" + index);

    elemToDelete.parentElement.removeChild(elemToDelete);
}

function checkAndRemove(type, index) {
    if (type && index && type == COMP) {
        removeDescendants(type, index)
    }
}