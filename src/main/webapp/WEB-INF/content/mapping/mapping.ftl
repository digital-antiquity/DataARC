  <nav class="navbar navbar-default">
    <div class="container-fluid">
      <a class="navbar-brand"><i class="glyphicon glyphicon-bullhorn"></i> Vue.js</a>
    </div>
  </nav>
  <div class="container" id="events">
    <div class="col-sm-7">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3>Events</h3>
        </div>
        <div class="panel-body">
          <div class="col-sm-8">
            <input class="form-control" placeholder="title" v-model="event.title">
            <textarea class="form-control" placeholder="detail" v-model="event.detail"></textarea>
            <input type="date" class="form-control" placeholder="date" v-model="event.date">
            <button class="btn btn-primary" v-on:click="addEvent">add</button>
        </div>
          <div class="col-sm-4">
            <p><b>{{event.title}}</b></p>
            <p>{{event.detail}}</p>
            <p><i>{{event.date}}</i></p>
          </div>
        </div>
      </div>
    </div>
    <div class="col-sm-5">
      <div class="list-group">
        <a href="#" class="list-group-item" v-for="event in events">
          <h4 class="list-group-item-heading"><i class="glyphicon glyphicon-bullhorn"></i> {{ event.title }}</h4>
          <h5><i class="glyphicon glyphicon-calendar" v-if="event.date"></i> {{ event.date }}</h5>
          <p class="list-group-item-text" v-if="event.detail">{{ event.detail }}</p>
          <button class="btn btn-xs btn-danger" v-on:click="deleteEvent($index)">delete</button>
        </a>
      </div>
    </div>
    <button class="btn btn-xs btn" v-on:click="getData()">json?</button>
  </div>
  
  <script src="/js/app/mapping/mapping.js"></script>
