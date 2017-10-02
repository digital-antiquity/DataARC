var timeline_data;

// Page Level Javascript Actions

$(document).ready(function() {

  // Initialize random data for the demo
  var now = moment().endOf('day').toDate();
  var time_ago = moment().startOf('day').subtract(10, 'year').toDate();
  timeline_data = d3.timeDays(time_ago, now).map(function (dateElement, index) {
    return {
      date: dateElement,
      details: Array.apply(null, new Array(Math.floor(Math.random() * 15))).map(function(e, i, arr) {
        return {
          'name': 'Project ' + Math.round(Math.random() * 10),
          'date': function () {
            var projectDate = new Date(dateElement.getTime());
            projectDate.setHours(Math.floor(Math.random() * 24))
            projectDate.setMinutes(Math.floor(Math.random() * 60));
            return projectDate;
          }(),
          'value': 3600 * ((arr.length - i) / 5) + Math.floor(Math.random() * 3600) * Math.round(Math.random() * (index / 365))
        }
      }),
      init: function () {
        this.total = this.details.reduce(function (prev, e) {
          return prev + e.value;
        }, 0);
        return this;
      }
    }.init();
  });






  // Smooth scrolling using jQuery easing
  $('a.js-scroll-trigger[href*="#"]:not([href="#"])').click(function() {
    if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
      if (target.length) {
        $('html, body').animate({
          scrollTop: (target.offset().top - 48)
        }, 1000, "easeInOutExpo");
        return false;
      }
    }
  });

  // Closes responsive menu when a scroll trigger link is clicked
  $('.js-scroll-trigger').click(function() {
    $('.navbar-collapse').collapse('hide');
  });

  // Activate scrollspy to add active class to navbar items on scroll
  $('body').scrollspy({
    target: '#mainNav',
    offset: 48
  });

  // Collapse the navbar when page is scrolled
  $(window).scroll(function() {
    if ($("#mainNav").offset().top > 100) {
      $("#mainNav").addClass("navbar-shrink");
    } else {
      $("#mainNav").removeClass("navbar-shrink");
    }
  });

  // Scroll reveal calls
  window.sr = ScrollReveal();
  sr.reveal('.sr-icons', {
    duration: 600,
    scale: 0.3,
    distance: '0px'
  }, 200);
  sr.reveal('.sr-button', {
    duration: 1000,
    delay: 200
  });
  sr.reveal('.sr-contact', {
    duration: 600,
    scale: 0.3,
    distance: '0px'
  }, 300);

  // Magnific popup calls
  $('.popup-gallery').magnificPopup({
    delegate: 'a',
    type: 'image',
    tLoading: 'Loading image #%curr%...',
    mainClass: 'mfp-img-mobile',
    gallery: {
      enabled: true,
      navigateByImgClick: true,
      preload: [0, 1]
    },
    image: {
      tError: '<a href="%url%">The image #%curr%</a> could not be loaded.'
    }
  });

  // Update keywords on button click
  $('#keywords-btn').click(function() {
    Search.set("keywords", [$('#keywords-field').val()]);
  });
});