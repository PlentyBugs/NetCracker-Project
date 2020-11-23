var colors = ["#F7464A", "#46BFBD", "#FDB45C", "#9b59cb"];

let winCount=$("#winCount");
let secondCount=$("#secondCount");
let thirdCount=$("#thirdCount");
let spotted=$("#spotted");

var donutOptions = {
    cutoutPercentage: 55,
    legend: {position:'bottom',
        labels:{pointStyle:'circle',
            usePointStyle:true}
    }
};
var chDonutData1 = {
    labels: ['Wins', '2nd', '3rd','Spotted'],
    datasets: [
        {
            backgroundColor: [colors[0],colors[1],colors[2],colors[3]],
            borderWidth: 0,
            data: [winCount, secondCount, thirdCount,spotted]
        }
    ]
};
var chDonut1 = $("#chDonut");

if (chDonut1) {
    new Chart(chDonut1, {
        type: 'pie',
        data: chDonutData1,
        options: donutOptions
    });
}
