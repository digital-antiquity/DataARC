<h4>      SLEECT!</h4>
  <div class="container" id="schema">

        <div class="col-sm-12">
	      Schema: <select v-model="selectedSchema" >
	          <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
	      </select>
	      Field: <select v-model="selectedField" >
	          <option v-for="(option, index) in fields" v-bind:value="index"> {{ option.name }} </option>
	      </select>
	      </div>
	        <div class="col-sm-12">
	      
	      <ul v-for="value in uniqueValues">
	       <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
	      </ul>
	   </div>
{{queryParts}}
  <div class="container-fluid">
      <spart v-for="part in queryParts" :part="part" :fields="fields"></spart>
    <ul class="list-group" v-for="part in queryParts">
    	 <div>
 	<select name='fieldName'>
          <option v-for="(field, index) in fields" v-bind:value="field.name"> {{ field.name }} </option>
 	</select>

 	 	<select name='type'>
 		<option value="EQUALS">Equals</option>
 		<option value="GREATER_THAN">Greater Than</option>
 		<option value="LESS_THAN">Less Than</option>
 	</select>
 	<input name='value' value="" />
 </div>

    </ul>
  </div>
</div>
  
            <button class="btn btn-xs btn" v-on:click="getData()">json?</button>
  <script src="/js/app/mapping/mapping.js"></script>


<!--
 -->

<template id="spart-template">
hi {{part}}
</template>