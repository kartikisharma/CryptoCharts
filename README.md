# SymbilityIntersect-CodeChallenge1

<p>This is my implementation of all the features requested by the mobile engineer challenge for the Android platform. The following are additional features, I added to optimize the behaviour:
<ul>
    <li>Snackbar to notify if the user is disconnected from networking and attempting API calls. This is dismissed when internet becomes available during consequent API calls.</li>
</ul>
</p>

<h3>Tools/Libraries Used:</h3>
<ul>
  <li>Butterknife</li>
  <li>Retrofit</li>
  <li>Room</li>
  <li>RxJava</li>
</ul>

<h3>Challenges</h3>
<ol>
  <li> 
    <b>Deciding When to Fetch Price Responses</b>
    <p>I debated whether to parse all the prices before the list of coins was shown. However, due to the large amount of calls (approx. 2000), it would take about 30 secs to have all data ready, diminishing the user experience. Thus, I immediately switched to getting the price when the view was being bounded in the list (and doing it on a background thread as well).</p>
    <p> <b>Unsuccessful API calls:</b>
      I hid the price completely, and later added in a snackbar to notify if it was caused by no network connection. I left the price as null, to ensure another API call could be made on the next binding.</a>
    </p>
  </li>
  <li>
    <b>Using Unoptimal Approach For Splash Screen</b>
    <p>Due to the requirements asking for text on the splash screen and lack of resources tweaking the optimal approach, I had to take the alternate approach which had some edge cases resulting in bugs. Luckily, I was able to find a <a href="https://medium.com/@AkhilDad/splash-screens-usability-and-common-bug-71c6bea33f20">Medium post<a> discussing these issues and their resolution.</p>
  </li>
  <li>
    <b>What to Store in Database</b>
    <p>Due to the large amount of coin data, I debated whether it was worth saving some of the unfavourited data such that user can observe it when no connection is available for the initial API call. However, I didn't think it would useful as the price data would be old and meaningless to provide functionalities for it. Hence, I only stored the favourited coin data in the database and only showed those when no connection is available <b>for the initial API call. See screenshots below for its UI state.</b>
    </p>
  </li>
</ol>

<h3>Drawbacks</h3>
<ul>
  <li> 
    <b>Using Recycler View & Making API calls for Missing Prices</b>
    <p>Some price API responses are susceptible of arriving later or failing completely. Hence, changing the UI of the price is somewhat noticable but nonetheless quick; it came at cost of minimizing the aforementioned time to prepare the list.</p>
  </li>
</ul> 

<h3>Screenshots</h3>
<div display="inline-block">
  <img src="screenshots/optimalRecyclerViewState.png" width="350" height="600" hspace="20">
  <img src="screenshots/noConnectionRecyclerViewState.png" width="350" height="600">
  <img src="screenshots/mainAPIcallfailedDB.png" width="350" height="600">
</div>
