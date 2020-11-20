var colors = ["#F7464A", "#46BFBD", "#FDB45C", "#949FB1"];

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

            backgroundColor: colors.slice(0,3),
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
