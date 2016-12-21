  <nav class="navbar navbar-default">
    <div class="container-fluid">
      <a class="navbar-brand"><i class="glyphicon glyphicon-bullhorn"></i> Vue.js</a>
    </div>
  </nav>
  <div class="container" id="schema">
    <div class="col-sm-5">
      <div class="list-group">
        <a href="#" class="list-group-item" v-for="scheme_ in schema">
          <h4 class="list-group-item-heading"><i class="glyphicon glyphicon-bullhorn"></i> {{ scheme_.name }}</h4>
          <button class="btn btn-xs btn-danger" v-on:click="deleteEvent($index)">delete</button>
        </a>
      </div>
    </div>
    <button class="btn btn-xs btn" v-on:click="getData()">json?</button>
  </div>
  
  <script src="/js/app/mapping/mapping.js"></script>
