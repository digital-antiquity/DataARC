<h4>      SLEECT!</h4>
  <div class="container" id="schema">

        <div class="col-sm-5">
      <select v-model="selectedSchema" >
          <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
      </select>
      <select v-model="selectedField" >
          <option v-for="(option, index) in fields" v-bind:value="option.id"> {{ option.name }} </option>
      </select>
   </div>

  </div>
  
            <button class="btn btn-xs btn" v-on:click="getData()">json?</button>
  <script src="/js/app/mapping/mapping.js"></script>
