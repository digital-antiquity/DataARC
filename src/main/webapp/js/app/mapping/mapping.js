// var Vue = require('vue');

var Hack = new Vue({
  el: '#schema',

  data: {
    schemum: { name: ''},
    schema: []
  },

  mounted: function () {
    this.fetchEvents();
  },

  methods: {

    fetchEvents: function () {
      var events = [];
      console.log("HI");
      this.$http.get('/api/schema/list')
        .then(function (schema) {
            console.log(JSON.stringify(schema.body));
            var list = new Array();
            schema.body.forEach(function(s) {
                list.push({name: s});
            });
          Vue.set(this, 'schema', list);
        })
        .catch(function (err) {
          console.err(err);
        });
    },

//    addEvent: function () {
//      if (this.event.title.trim()) {
//        // this.events.push(this.event);
//        // this.event = { title: '', detail: '', date: '' };
//        this.$http.post('/api/events', this.event)
//          .success(function (res) {
//            this.events.push(this.event);
//            console.log('Event added!');
//          })
//          .error(function (err) {
//            console.log(err);
//          });
//      }
//    },
//
//    deleteEvent: function (index) {
//      if (confirm('delete？')) {
//        // this.events.splice(index, 1);
//        this.$http.delete('api/events/' + event.id)
//          .success(function (res) {
//            console.log(res);
//            this.events.splice(index, 1);
//          })
//          .error(function (err) {
//            console.log(err);
//          });
//      }
//    },
    
    getData: function() {
        console.log(this.schema);
        return this.schema;
    }
  }
});