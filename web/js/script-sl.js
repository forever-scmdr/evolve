const gap = 16;

const carousel = document.getElementsByClassName("vl_c_carousel");
function initCarousel(c) {
    var content = c.getElementsByClassName("vl_c_content")[0];
    var next = c.parentElement.getElementsByClassName("vl_c_next")[0];
    var prev = c.parentElement.getElementsByClassName("vl_c_prev")[0];

    next.addEventListener("click", e => {
        c.scrollBy(width + gap, 0);
        if (c.scrollWidth !== 0) {
            prev.style.display = "flex";
        }
        if (content.scrollWidth - width - gap <= c.scrollLeft + width) {
            next.style.display = "none";
        }
    });
    prev.addEventListener("click", e => {
        c.scrollBy(-(width + gap), 0);
        if (c.scrollLeft - width - gap <= 0) {
            prev.style.display = "none";
        }
        if (!content.scrollWidth - width - gap <= c.scrollLeft + width) {
            next.style.display = "flex";
        }
    });

    let width = c.offsetWidth;
    window.addEventListener("resize", e => (width = c.offsetWidth));
}

for (i = 0; carousel.length > i; i++) {
    c = carousel[i];
    initCarousel(c);
}