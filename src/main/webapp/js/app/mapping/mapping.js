// var Vue = require('vue');
  Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
  }

var Hack = new Vue({
  el: '#schema',

  data: {
    schemum: { name: ''},
    schema: [],
    fields: [],
    selectedSchema: undefined,
    selectedField: undefined
  },
  computed: {
      selectedFields: function() {
          console.log("calculating selected fields ..." + this.selectedSchema);
          if (this.selectedSchema == undefined || schema[this.selectedSchema] == undefined) {
              return [];
          }
          return this.schema[this.selectedSchema].fields;
      }
  },
  watch: {
      'selectedSchema' : function(val, oldVal) {
          this.selectSchema();
      }
  },
  mounted: function () {
    this.fetchSchema();
  },

  methods: {

      fetchSchema: function () {
          var events = [];
          console.log("fetch schema");
          this.$http.get('/api/schema/list')
            .then(function (schema) {
//                console.trace(JSON.stringify(schema.body));
                var list = new Array();
                schema.body.forEach(function(s) {
                    list.push({name: s});
                });
                console.log(list);
              Vue.set(this, 'schema', list);
            })
            .catch(function (err) {
                console.log(err);
              console.err(err);
            });
        },
        selectSchema: function () {
            console.log("hi");
            var s = this.schema[this.selectedSchema];
            console.log("fetch fields for "+ s.name);
            this.$http.get('/api/schema/listFields',{params: {'schema': s.name}})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
                Vue.set(this, 'fields', request.body);
              })
              .catch(function (err) {
                  console.log(err);
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
