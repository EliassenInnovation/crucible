function td(content,type,className) {
    let td = document.createElement("td");

    if (type !== undefined && type == "html") {
        td.appendChild(content)
    } else {
        td.innerText = content;
    }

    if (className !== undefined) {
        td.className = className;
    }
    return td;
}

function p() {
    return document.createElement("p");
}

function span(content) {
    let span = document.createElement("span")
    span.innerHTML = content;
    return span;
}

function b(content) {
    let b = document.createElement("b");

    b.innerText = content;

    return b;
}